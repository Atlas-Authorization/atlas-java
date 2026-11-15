package net.atlasauth.atlas.machine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.http.Json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * The device side of machine enrolment, for a service or device of your own: generate an Ed25519 key, enrol
 * with a token or a short code, then trade a signed challenge for short-lived machine tokens, cached until
 * shortly before they expire. It speaks the three-POST wire protocol every Atlas machine client speaks.
 *
 * <pre>{@code
 * MachineClient machine = MachineClient.builder("https://clerk.example.com", MachineStore.file(path)).build();
 * if (!machine.isEnrolled()) machine.enroll("met_...", "build-agent", null, null);
 * MachineClient.Token token = machine.getToken(new MachineClient.TokenRequest().audience("https://api.example.com"));
 * // Authorization: Bearer <token.token()>
 * }</pre>
 */
public final class MachineClient {

    /** What {@link #enroll} returns. {@code approvalRequired}: an administrator must approve the machine before it can get tokens. */
    public record EnrolResult(String machineId, String status, boolean approvalRequired) {
    }

    /** A machine token. {@code tokenType} is {@code DPoP} when it is bound to this client's key. */
    public record Token(String token, String tokenType, long expiresAt, String audience, String scope,
                        Map<String, Object> constraints, String organizationId) {
    }

    /** What the token is for. {@code scopes} and {@code scopePresets} need an {@code audience}. */
    public static final class TokenRequest {
        private String audience;
        private List<String> scopes;
        private List<String> scopePresets;
        private String organizationId;

        public TokenRequest audience(String audience) {
            this.audience = audience;
            return this;
        }

        public TokenRequest scopes(List<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        public TokenRequest scopePresets(List<String> scopePresets) {
            this.scopePresets = scopePresets;
            return this;
        }

        public TokenRequest organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        String cacheKey() {
            List<String> s = new ArrayList<>(scopes == null ? List.of() : scopes);
            List<String> p = new ArrayList<>(scopePresets == null ? List.of() : scopePresets);
            Collections.sort(s);
            Collections.sort(p);
            return String.join("\u0000", audience == null ? "" : audience, String.join(",", s), String.join(",", p), organizationId == null ? "" : organizationId);
        }
    }

    private final String issuer;
    private final MachineStore store;
    private final HttpClient http;
    private final LongSupplier now;
    private final long skewMs;
    private final DpopKey dpopKey;
    private final ObjectMapper mapper = Json.mapper();
    private final Map<String, Token> cache = new ConcurrentHashMap<>();
    private final Map<String, Object> locks = new ConcurrentHashMap<>();

    private MachineClient(Builder b) {
        this.issuer = b.issuer.replaceAll("/+$", "");
        this.store = b.store;
        this.http = b.http != null ? b.http : HttpClient.newHttpClient();
        this.now = b.now;
        this.skewMs = b.refreshSkewSeconds * 1000L;
        this.dpopKey = b.dpopKey;
    }

    public static Builder builder(String issuer, MachineStore store) {
        return new Builder(issuer, store);
    }

    /** {@code base64url(sign(privateKey, nonce))}: pure Ed25519 over the nonce's UTF-8 bytes, no padding. */
    public static String signChallenge(String privateKeyPem, String nonce) {
        try {
            PrivateKey key = java.security.KeyFactory.getInstance("Ed25519").generatePrivate(new PKCS8EncodedKeySpec(DpopKey.unpem(privateKeyPem)));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(DpopKey.sign(key, nonce.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("the machine key is not an Ed25519 PKCS#8 PEM", e);
        }
    }

    /** Generate a key, enrol with a {@code met_} token or a short code ({@code XXXX-XXXX}), and store the identity. Only the public half is sent. */
    public EnrolResult enroll(String credential, String name, Map<String, Object> metadata, String deviceKey) {
        try {
            KeyPair kp = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
            String publicPem = DpopKey.pem("PUBLIC KEY", kp.getPublic().getEncoded());
            String privatePem = DpopKey.pem("PRIVATE KEY", kp.getPrivate().getEncoded());
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("enrolment_token", credential.trim());
            body.put("name", name);
            body.put("public_key_pem", publicPem);
            if (metadata != null) {
                body.put("metadata", metadata);
            }
            if (deviceKey != null) {
                body.put("device_key", deviceKey);
            }
            JsonNode res = post("/v1/machines/enroll", body, false);
            long enrolledAt = res.hasNonNull("enrolled_at") ? res.get("enrolled_at").asLong() : now.getAsLong();
            store.save(new MachineIdentity(issuer, res.path("id").asText(), privatePem, publicPem, enrolledAt));
            cache.clear();
            return new EnrolResult(res.path("id").asText(), res.path("status").asText(), res.path("approval_required").asBoolean(false));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Ed25519 is not available in this JVM", e);
        }
    }

    public boolean isEnrolled() {
        return identity() != null;
    }

    /** The enrolled machine id, or null. */
    public String machineId() {
        MachineIdentity id = identity();
        return id == null ? null : id.machineId();
    }

    /**
     * A machine token for this request, from the cache while it has more than the refresh skew left.
     * Concurrent callers asking for the same thing share one mint.
     */
    public Token getToken(TokenRequest request) {
        TokenRequest req = request == null ? new TokenRequest() : request;
        String key = req.cacheKey();
        Token hit = cache.get(key);
        if (hit != null && hit.expiresAt() - skewMs > now.getAsLong()) {
            return hit;
        }
        synchronized (locks.computeIfAbsent(key, k -> new Object())) {
            hit = cache.get(key);
            if (hit != null && hit.expiresAt() - skewMs > now.getAsLong()) {
                return hit;
            }
            Token minted = mint(req);
            cache.put(key, minted);
            return minted;
        }
    }

    /** A token for the default audience and scopes. */
    public Token getToken() {
        return getToken(null);
    }

    /** A DPoP proof for one call to your API with a token this client minted. Needs the {@code dpopKey} builder option. */
    public String dpopProof(String token, String method, String url, String nonce) {
        if (dpopKey == null) {
            throw new MachineClientException(0, "dpop_not_configured", "Create the client with the dpopKey option to sign DPoP proofs.");
        }
        return dpopKey.proof(method, url, token, nonce, now.getAsLong());
    }

    /** The thumbprint ({@code cnf.jkt}) tokens minted by this client carry, or null without a {@code dpopKey}. */
    public String dpopThumbprint() {
        return dpopKey == null ? null : dpopKey.thumbprint();
    }

    /** Forget cached tokens (the stored identity stays). */
    public void clearTokens() {
        cache.clear();
    }

    /** Forget the identity too: the machine must enrol again. */
    public void reset() {
        cache.clear();
        store.clear();
    }

    private MachineIdentity identity() {
        MachineIdentity id = store.load();
        return id != null && issuer.equals(id.issuer()) ? id : null;
    }

    private Token mint(TokenRequest req) {
        MachineIdentity id = identity();
        if (id == null) {
            throw new MachineClientException(0, "not_enrolled", "This machine is not enrolled. Call enroll() first.");
        }
        JsonNode challenge = post("/v1/machines/challenge", Map.of("machine_id", id.machineId()), false);
        String nonce = challenge.path("nonce").asText();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("machine_id", id.machineId());
        body.put("nonce", nonce);
        body.put("signature", signChallenge(id.privateKeyPem(), nonce));
        if (req.audience != null) {
            body.put("audience", req.audience);
        }
        if (req.scopes != null) {
            body.put("scopes", req.scopes);
        }
        if (req.scopePresets != null) {
            body.put("scope_presets", req.scopePresets);
        }
        if (req.organizationId != null) {
            body.put("organization_id", req.organizationId);
        }
        JsonNode res = post("/v1/machines/token", body, true);
        @SuppressWarnings("unchecked")
        Map<String, Object> constraints = res.has("constraints") ? mapper.convertValue(res.get("constraints"), Map.class) : null;
        return new Token(res.path("token").asText(), "DPoP".equals(res.path("token_type").asText()) ? "DPoP" : "Bearer",
                now.getAsLong() + res.path("expires_in").asLong() * 1000, res.path("audience").asText(null), res.path("scope").asText(null),
                constraints, res.path("organization_id").asText(null));
    }

    private JsonNode post(String path, Map<String, Object> body, boolean proof) {
        String url = issuer + path;
        try {
            HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30))
                    .header("content-type", "application/json").header("accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body), StandardCharsets.UTF_8));
            if (proof && dpopKey != null) {
                b.header("dpop", dpopKey.proof("POST", url, null, null, now.getAsLong()));
            }
            HttpResponse<String> res = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
            JsonNode json;
            try {
                json = res.body() == null || res.body().isEmpty() ? mapper.createObjectNode() : mapper.readTree(res.body());
            } catch (IOException notJson) {
                json = mapper.createObjectNode();
            }
            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                JsonNode first = json.path("errors").isArray() && json.get("errors").size() > 0 ? json.get("errors").get(0) : null;
                String code = first != null && first.hasNonNull("code") ? first.get("code").asText() : json.path("code").asText(null);
                String message = first != null && first.hasNonNull("message") ? first.get("message").asText()
                        : json.path("message").asText("Request failed with status " + res.statusCode() + ".");
                throw new MachineClientException(res.statusCode(), code, message);
            }
            return json;
        } catch (IOException e) {
            throw new MachineClientException(0, "network_error", "Request to " + url + " failed: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MachineClientException(0, "interrupted", "Request to " + url + " was interrupted");
        }
    }

    public static final class Builder {
        private final String issuer;
        private final MachineStore store;
        private HttpClient http;
        private LongSupplier now = System::currentTimeMillis;
        private int refreshSkewSeconds = 60;
        private DpopKey dpopKey;

        private Builder(String issuer, MachineStore store) {
            this.issuer = issuer;
            this.store = store;
        }

        public Builder httpClient(HttpClient http) {
            this.http = http;
            return this;
        }

        /** Clock, epoch ms (tests). */
        public Builder clock(LongSupplier now) {
            this.now = now;
            return this;
        }

        /** Fetch a new token this long before the cached one expires. Default 60 seconds. */
        public Builder refreshSkewSeconds(int seconds) {
            this.refreshSkewSeconds = seconds;
            return this;
        }

        /** Bind every token to this DPoP key (RFC 9449). Needed when the machine's token binding is {@code dpop}. */
        public Builder dpopKey(DpopKey key) {
            this.dpopKey = key;
            return this;
        }

        public MachineClient build() {
            return new MachineClient(this);
        }
    }
}
