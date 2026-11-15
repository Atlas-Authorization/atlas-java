package net.atlasauth.atlas.mirror;

import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.AtlasApiException;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;

/**
 * A local, authoritative mirror of end-user API keys ({@code ak_}), with Atlas as the minting and revocation
 * backstop.
 *
 * <p>Your request path verifies a presented key against the LOCAL row (sha256 hash, scopes, expiry,
 * revocation) with no network call. Atlas mints and imports keys and is told about every revoke; a revoke
 * made anywhere else (the dashboard, the user's own key page, another service) reaches the mirror through
 * {@link #sync()}, which consumes the {@code /v1/api_keys/revoked} feed, or {@link #applyWebhook}. Revoking
 * locally happens FIRST, so a failed Atlas call never leaves a key usable here.
 *
 * <pre>{@code
 * KeyMirror mirror = KeyMirror.builder(Backend.of(atlas), new MemoryKeyMirrorStore())
 *     .maxStalenessMs(300_000).build();
 * mirror.sync();                                      // on a timer, every few seconds to a minute
 * KeyMirror.Verification v = mirror.verify(secret, List.of("reports:read"), clientIp);
 * if (!v.valid()) { ... }
 * }</pre>
 */
public final class KeyMirror {

    /** A membership of the user in the organization a key is bound to, from your own data. */
    public record Membership(List<String> permissions, Map<String, Object> data) {
    }

    /** The answer of {@link #verify}. {@code code} is null for a plain invalid key (unknown, revoked, expired). */
    public record Verification(boolean valid, String code, MirroredApiKey key, List<String> effectiveScopes,
                               List<String> missingScopes, boolean stale, Map<String, Object> organization, Long lastSyncAt) {
        static Verification invalid(String code) {
            return new Verification(false, code, null, List.of(), List.of(), false, null, null);
        }
    }

    private static final Pattern HEX64 = Pattern.compile("^[0-9a-f]{64}$");

    private final Backend backend;
    private final KeyMirrorStore store;
    private final Map<String, List<String>> implications;
    private final List<String> allowedScopes;
    private final boolean verifyAtAtlas;
    private final Long maxStalenessMs;
    private final long serveStaleForMs;
    private final StaleListener onServeStale;
    private final BiFunction<String, String, Membership> membership;
    private final LongSupplier now;
    private volatile Long memorySyncAt;

    /** Called each time an answer is served inside the {@code serveStaleForMs} grace. */
    @FunctionalInterface
    public interface StaleListener {
        void served(Long lastSyncAt, long staleForMs);
    }

    private KeyMirror(Builder b) {
        this.backend = b.backend;
        this.store = b.store;
        this.implications = b.implications;
        this.allowedScopes = b.allowedScopes;
        this.verifyAtAtlas = b.verifyAtAtlas;
        this.maxStalenessMs = b.maxStalenessMs;
        this.serveStaleForMs = b.serveStaleForMs;
        this.onServeStale = b.onServeStale;
        this.membership = b.membership;
        this.now = b.now;
    }

    public static Builder builder(Backend backend, KeyMirrorStore store) {
        return new Builder(backend, store);
    }

    /** sha256 hex: the hash the mirror stores and Atlas imports as {@code sha256_hex}. */
    public static String hashApiKeySecret(String secret) {
        return hex(sha256(secret.getBytes(StandardCharsets.UTF_8)));
    }

    /** Whether a permission covers a key scope: same name, {@code *}, or {@code <prefix>:*}. Mirrors Atlas verify. */
    public static boolean permissionCoversScope(String permission, String scope) {
        if (permission.equals(scope) || permission.equals("*")) {
            return true;
        }
        return permission.endsWith(":*") && scope.startsWith(permission.substring(0, permission.length() - 1));
    }

    /** The key scopes a membership's permissions cover: what Atlas returns as {@code effective_scopes}. Sorted. */
    public static List<String> intersectScopesWithPermissions(List<String> scopes, List<String> permissions) {
        TreeSet<String> out = new TreeSet<>();
        for (String s : scopes) {
            if (permissions.stream().anyMatch(p -> permissionCoversScope(p, s))) {
                out.add(s);
            }
        }
        return new ArrayList<>(out);
    }

    /** Whether {@code ip} is inside one of the addresses / CIDR ranges. Unparseable input is never inside. */
    public static boolean ipInAllowlist(String ip, List<String> cidrs) {
        byte[] addr = ipBytes(ip);
        if (addr == null) {
            return false;
        }
        for (String entry : cidrs) {
            String[] parts = entry.trim().split("/", 2);
            byte[] net = ipBytes(parts[0]);
            if (net == null || net.length != addr.length) {
                continue;
            }
            int bits;
            try {
                bits = parts.length == 1 ? net.length * 8 : Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                continue;
            }
            if (bits < 0 || bits > net.length * 8) {
                continue;
            }
            boolean inside = true;
            for (int i = 0; i < net.length && inside; i++) {
                int n = Math.min(8, Math.max(0, bits - i * 8));
                int mask = (0xff << (8 - n)) & 0xff;
                inside = (addr[i] & mask) == (net[i] & mask);
            }
            if (inside) {
                return true;
            }
        }
        return false;
    }

    private static byte[] ipBytes(String text) {
        String t = text == null ? "" : text.trim();
        // literals only: never resolve a host name
        if (!(t.matches("^\\d{1,3}(\\.\\d{1,3}){3}$") || (t.contains(":") && t.matches("^[0-9a-fA-F:.]+$")))) {
            return null;
        }
        try {
            return InetAddress.getByName(t).getAddress();
        } catch (java.net.UnknownHostException | RuntimeException e) {
            return null;
        }
    }

    /** Mint at Atlas, then mirror it. Returns Atlas's answer, which carries the one-time {@code secret}. */
    public JsonNode mint(Map<String, Object> body, String idempotencyKey) {
        JsonNode created = backend.createApiKey(body, idempotencyKey);
        store.put(toMirrored(created, hashApiKeySecret(created.path("secret").asText())));
        return created;
    }

    /**
     * Rotate at Atlas, then mirror both sides: the successor is added and the old key now ends at the end of
     * the overlap (or is revoked at once when {@code overlap_seconds} is 0). Returns the answer with the
     * successor's one-time {@code secret}.
     */
    public JsonNode rotate(String id, Map<String, Object> body, String idempotencyKey) {
        JsonNode rotated = backend.rotateApiKey(id, body == null ? Map.of() : body, idempotencyKey);
        store.put(toMirrored(rotated, hashApiKeySecret(rotated.path("secret").asText())));
        MirroredApiKey old = store.get(id);
        if (old != null) {
            JsonNode prev = rotated.path("previous_key");
            if (prev.path("revoked").asBoolean(false)) {
                store.markRevoked(id, now.getAsLong(), "rotated");
            } else if (prev.hasNonNull("expires_at")) {
                store.put(new MirroredApiKey(old.id(), old.hash(), old.prefix(), old.subjectType(), old.subjectId(), old.name(),
                        old.scopes(), old.allowedIps(), prev.get("expires_at").asLong(), old.revokedAt(), old.organizationId(), old.revokedReason()));
            }
        }
        return rotated;
    }

    /**
     * Import an existing key so customers keep their secret: pass the full {@code secret} (hashed here, never
     * sent) or its {@code secretHash} (64 lowercase hex), or an existing {@code bcryptHash} / {@code argon2idHash}
     * (kept as-is, verified at Atlas on first use with {@code verifyAtAtlas}).
     */
    public JsonNode importKey(Map<String, Object> body, String secret, String secretHash, String bcryptHash, String argon2idHash,
                              String prefix, String idempotencyKey) {
        Map<String, Object> request = new LinkedHashMap<>(body);
        String legacy = bcryptHash != null ? bcryptHash : argon2idHash;
        if (legacy != null) {
            request.put("import", Map.of("secret_hash", legacy, "hash_algorithm", bcryptHash != null ? "bcrypt" : "argon2id", "prefix", prefix));
            JsonNode imported = backend.createApiKey(request, idempotencyKey);
            boolean already = "already_present".equals(imported.path("outcome").asText());
            if (!already || store.get(imported.path("id").asText()) == null) {
                store.put(toMirrored(imported, "legacy:" + imported.path("id").asText()));
            }
            return imported;
        }
        String hash = secretHash != null ? secretHash.toLowerCase() : secret != null ? hashApiKeySecret(secret) : null;
        if (hash == null || !HEX64.matcher(hash).matches()) {
            throw new IllegalArgumentException("import needs the full secret or its secretHash (64 lowercase hex characters of sha256).");
        }
        request.put("import", Map.of("secret_hash", hash, "hash_algorithm", "sha256_hex", "prefix", prefix));
        JsonNode imported = backend.createApiKey(request, idempotencyKey);
        store.put(toMirrored(imported, hash));
        return imported;
    }

    /** Epoch ms of the last completed {@link #sync()}; null = never synced. */
    public Long lastSyncAt() {
        Long stored = store.getLastSyncAt();
        return stored != null ? stored : memorySyncAt;
    }

    /**
     * Revoke LOCALLY first (the key stops working here at once), then at Atlas. A key Atlas already revoked or
     * no longer knows (404/409) counts as revoked; any other Atlas error is thrown after the local revoke, so
     * retry it.
     */
    public void revoke(String id) {
        store.markRevoked(id, now.getAsLong(), null);
        try {
            backend.deleteApiKey(id);
        } catch (AtlasApiException e) {
            if (e.status() != 404 && e.status() != 409) {
                throw e;
            }
        }
    }

    public Verification verify(String secret) {
        return verify(secret, List.of(), null);
    }

    /**
     * Verify a presented secret against the mirror: no network call (unless {@code verifyAtAtlas} or a bound key
     * needs Atlas). Unknown, revoked and expired keys answer an invalid result with no code; a mirror that is
     * not fresh (see {@code maxStalenessMs}) answers {@code code = "stale"}. Other codes: {@code insufficient_scope},
     * {@code ip_not_allowed}, {@code client_ip_required}, {@code membership_unresolved}.
     *
     * @param clientIp the END caller's address, needed for a key with an IP allowlist (it fails closed without one)
     */
    public Verification verify(String secret, List<String> requiredScopes, String clientIp) {
        if (secret == null || secret.isEmpty()) {
            return Verification.invalid(null);
        }
        boolean servedStale = false;
        if (maxStalenessMs != null) {
            Long last = lastSyncAt();
            double behind = last == null ? Double.POSITIVE_INFINITY : now.getAsLong() - last - maxStalenessMs;
            if (behind > 0) {
                if (last == null || behind > serveStaleForMs) {
                    return new Verification(false, "stale", null, List.of(), List.of(), false, null, last);
                }
                servedStale = true;
                if (onServeStale != null) {
                    onServeStale.served(last, (long) behind);
                }
            }
        }
        String hash = hashApiKeySecret(secret);
        MirroredApiKey key = store.getByHash(hash);
        if (key == null && verifyAtAtlas) {
            JsonNode remote = backend.verifyApiKey(secret, clientIp);
            if (remote.path("valid").asBoolean(false)) {
                key = toMirrored(backend.getApiKey(remote.path("id").asText()), hash);
                store.put(key);
            }
        }
        if (key == null || key.revokedAt() != null) {
            return Verification.invalid(null);
        }
        if (key.expiresAt() != null && key.expiresAt() <= now.getAsLong()) {
            return Verification.invalid(null);
        }
        if (key.allowedIps() != null && !key.allowedIps().isEmpty()) {
            if (clientIp == null || clientIp.isEmpty()) {
                return Verification.invalid("client_ip_required");
            }
            if (!ipInAllowlist(clientIp, key.allowedIps())) {
                return Verification.invalid("ip_not_allowed");
            }
        }
        List<String> effective = expand(key.scopes() == null ? List.of() : key.scopes());
        Map<String, Object> organization = null;
        if ("user".equals(key.subjectType()) && key.organizationId() != null && !key.organizationId().isEmpty()) {
            // A bound key stands for the user's CURRENT membership; never answer from the key's own scopes.
            if (membership != null) {
                Membership held = membership.apply(key.subjectId(), key.organizationId());
                if (held == null) {
                    return Verification.invalid(null);
                }
                organization = held.data();
                effective = intersectScopesWithPermissions(effective, held.permissions());
            } else {
                JsonNode remote;
                try {
                    remote = backend.verifyApiKey(secret, clientIp);
                } catch (UnsupportedOperationException e) {
                    return Verification.invalid("membership_unresolved");
                }
                if (!remote.path("valid").asBoolean(false) || !remote.hasNonNull("organization")) {
                    return Verification.invalid(null);
                }
                organization = toMap(remote.get("organization"));
                List<String> remoteScopes = new ArrayList<>();
                remote.path("effective_scopes").forEach(n -> remoteScopes.add(n.asText()));
                effective = remoteScopes;
            }
        }
        List<String> missing = new ArrayList<>();
        for (String s : requiredScopes == null ? List.<String>of() : requiredScopes) {
            if (!effective.contains(s)) {
                missing.add(s);
            }
        }
        if (!missing.isEmpty()) {
            return new Verification(false, "insufficient_scope", null, List.of(), missing, false, null, null);
        }
        return new Verification(true, null, key, effective, List.of(), servedStale, organization, null);
    }

    /**
     * Consume the revocation feed from the stored cursor, revoking each listed key locally. Call it on a
     * timer (every few seconds to a minute): a key revoked at Atlas is rejected here within one tick.
     *
     * @return how many ids it applied
     */
    public int sync() {
        String since = store.getCursor();
        int revoked = 0;
        while (true) {
            JsonNode page = backend.listRevokedApiKeys(since, 100);
            for (JsonNode ref : page.path("data")) {
                store.markRevoked(ref.path("id").asText(), ref.hasNonNull("revoked_at") ? ref.get("revoked_at").asLong() : now.getAsLong(),
                        ref.hasNonNull("revoked_reason") ? ref.get("revoked_reason").asText() : null);
                revoked++;
            }
            String next = page.hasNonNull("next_cursor") ? page.get("next_cursor").asText() : null;
            if (next != null) {
                since = next;
                store.setCursor(next);
            }
            if (!page.path("has_more").asBoolean(false) || next == null) {
                memorySyncAt = now.getAsLong();
                store.setLastSyncAt(memorySyncAt);
                return revoked;
            }
        }
    }

    /**
     * Apply a VERIFIED {@code api_key.*} webhook (check the signature first). {@code api_key.revoked} revokes
     * locally; {@code api_key.restored} lifts a revocation a ban caused; {@code api_key.updated} refreshes
     * name, scopes and expiry of a key the mirror holds; a member removed from an organization drops the keys
     * bound to that membership. The hash never changes, so a key the mirror does not hold is ignored.
     *
     * @return whether the event changed the mirror
     */
    public boolean applyWebhook(String type, JsonNode data) {
        if (type == null || data == null) {
            return false;
        }
        if (type.equals("organization_membership.deleted") || type.equals("organizationMembership.deleted")) {
            if (data.path("user_id").isTextual() && data.path("organization_id").isTextual()) {
                return store.markMembershipEnded(data.get("user_id").asText(), data.get("organization_id").asText(), now.getAsLong());
            }
            return false;
        }
        if (!type.startsWith("api_key.") || !data.path("id").isTextual()) {
            return false;
        }
        MirroredApiKey held = store.get(data.get("id").asText());
        if (held == null) {
            return false;
        }
        Long expires = data.has("expires_at") ? (data.get("expires_at").isNull() ? null : data.get("expires_at").asLong()) : held.expiresAt();
        if (type.equals("api_key.restored")) {
            if (held.revokedAt() == null) {
                return false;
            }
            store.put(new MirroredApiKey(held.id(), held.hash(), held.prefix(), held.subjectType(), held.subjectId(), held.name(),
                    held.scopes(), held.allowedIps(), expires, null, held.organizationId(), null));
            return true;
        }
        if (type.equals("api_key.revoked") || (data.path("revoked_at").isNumber() && held.revokedAt() == null)) {
            store.markRevoked(held.id(), data.path("revoked_at").isNumber() ? data.get("revoked_at").asLong() : now.getAsLong(),
                    data.hasNonNull("revoked_reason") ? data.get("revoked_reason").asText() : null);
            return true;
        }
        if (type.equals("api_key.updated") || type.equals("api_key.created")) {
            store.put(new MirroredApiKey(held.id(), held.hash(), held.prefix(), held.subjectType(), held.subjectId(),
                    data.has("name") ? (data.get("name").isNull() ? null : data.get("name").asText()) : held.name(),
                    data.has("scopes") ? strings(data.get("scopes")) : held.scopes(),
                    data.has("allowed_ips") ? strings(data.get("allowed_ips")) : held.allowedIps(),
                    expires, held.revokedAt(), held.organizationId(), held.revokedReason()));
            return true;
        }
        return false;
    }

    private List<String> expand(List<String> held) {
        Set<String> vocab = new HashSet<>(allowedScopes);
        implications.forEach((from, tos) -> {
            vocab.add(from);
            tos.stream().filter(t -> !t.equals("*")).forEach(vocab::add);
        });
        Set<String> out = new LinkedHashSet<>(held);
        List<String> queue = new ArrayList<>(held);
        while (!queue.isEmpty()) {
            String cur = queue.remove(queue.size() - 1);
            for (String to : implications.getOrDefault(cur, List.of())) {
                for (String t : to.equals("*") ? vocab : Set.of(to)) {
                    if (out.add(t)) {
                        queue.add(t);
                    }
                }
            }
        }
        return new ArrayList<>(new TreeSet<>(out));
    }

    private static MirroredApiKey toMirrored(JsonNode k, String hash) {
        return new MirroredApiKey(k.path("id").asText(), hash, k.path("prefix").asText(null), k.path("subject_type").asText(null),
                k.path("subject_id").asText(null), k.hasNonNull("name") ? k.get("name").asText() : null,
                k.hasNonNull("scopes") ? strings(k.get("scopes")) : null, k.hasNonNull("allowed_ips") ? strings(k.get("allowed_ips")) : null,
                k.hasNonNull("expires_at") ? k.get("expires_at").asLong() : null, k.hasNonNull("revoked_at") ? k.get("revoked_at").asLong() : null,
                k.hasNonNull("organization_id") ? k.get("organization_id").asText() : null,
                k.hasNonNull("revoked_reason") ? k.get("revoked_reason").asText() : null);
    }

    private static List<String> strings(JsonNode arr) {
        if (arr == null || arr.isNull()) {
            return null;
        }
        List<String> out = new ArrayList<>();
        arr.forEach(n -> out.add(n.asText()));
        return out;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toMap(JsonNode node) {
        return new com.fasterxml.jackson.databind.ObjectMapper().convertValue(node, Map.class);
    }

    private static byte[] sha256(byte[] in) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(in);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) {
            sb.append(String.format("%02x", x));
        }
        return sb.toString();
    }

    public static final class Builder {
        private final Backend backend;
        private final KeyMirrorStore store;
        private Map<String, List<String>> implications = Map.of();
        private List<String> allowedScopes = List.of();
        private boolean verifyAtAtlas;
        private Long maxStalenessMs;
        private long serveStaleForMs;
        private StaleListener onServeStale;
        private BiFunction<String, String, Membership> membership;
        private LongSupplier now = System::currentTimeMillis;

        private Builder(Backend backend, KeyMirrorStore store) {
            this.backend = backend;
            this.store = store;
        }

        /** The instance scope implications, so a local scope check matches Atlas verify. {@code *} means every known scope. */
        public Builder scopeImplications(Map<String, List<String>> implications) {
            this.implications = implications;
            return this;
        }

        /** Scopes the instance allows (the vocabulary a {@code *} implication expands to). */
        public Builder allowedScopes(List<String> allowedScopes) {
            this.allowedScopes = allowedScopes;
            return this;
        }

        /** On a local miss ask Atlas to verify the secret (needed for bcrypt/argon2id imports); once accepted it is cached by sha256. */
        public Builder verifyAtAtlas(boolean verifyAtAtlas) {
            this.verifyAtAtlas = verifyAtAtlas;
            return this;
        }

        /** Refuse ({@code code = "stale"}) when no sync completed in this many ms: a revoked key may not have reached the mirror yet. */
        public Builder maxStalenessMs(long maxStalenessMs) {
            this.maxStalenessMs = maxStalenessMs;
            return this;
        }

        /** Keep answering from the last synced data for this many extra ms after the staleness bound; such answers carry {@code stale = true}. */
        public Builder serveStaleForMs(long serveStaleForMs, StaleListener onServeStale) {
            this.serveStaleForMs = serveStaleForMs;
            this.onServeStale = onServeStale;
            return this;
        }

        /** For a key bound to an organization: the user's CURRENT membership from your own data ({@code null} = no longer a member). */
        public Builder membership(BiFunction<String, String, Membership> membership) {
            this.membership = membership;
            return this;
        }

        /** Clock, for tests. */
        public Builder clock(LongSupplier now) {
            this.now = now;
            return this;
        }

        public KeyMirror build() {
            return new KeyMirror(this);
        }
    }
}
