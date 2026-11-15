package net.atlasauth.atlas.mirror;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.RSAKey;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * The signed session status list ({@code GET /v1/sessions/revocations/status-list}): a JWT ({@code typ statuslist+jwt})
 * signed with an instance key (RS256, PS256, ES256, ES384 or EdDSA) carrying the first {@code hash_bytes} bytes of the
 * SHA-256 of every session id revoked in its window, sorted, as one base64url string. {@link #verify} never returns a
 * half-trusted list: any problem is a {@link StatusListException}.
 */
public final class StatusList {

    /** Why a list was refused: malformed, unsupported, no_key, bad_signature, bad_claims or expired. */
    public static final class StatusListException extends RuntimeException {
        private final String reason;

        public StatusListException(String reason) {
            super("Invalid session status list: " + reason);
            this.reason = reason;
        }

        public String reason() {
            return reason;
        }
    }

    private static final ObjectMapper JSON = new ObjectMapper();
    private final byte[] list;
    private final int hashBytes;
    private final int count;
    private final boolean truncated;
    private final long issuedAt;
    private final long expiresAt;
    private final long since;

    private StatusList(byte[] list, int hashBytes, int count, boolean truncated, long issuedAt, long expiresAt, long since) {
        this.list = list;
        this.hashBytes = hashBytes;
        this.count = count;
        this.truncated = truncated;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.since = since;
    }

    /** True when the session id's hash is in the list. */
    public boolean has(String sid) {
        byte[] needle = Arrays.copyOf(sha256(sid.getBytes(StandardCharsets.UTF_8)), hashBytes);
        int lo = 0, hi = count - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int c = 0;
            for (int i = 0; i < hashBytes && c == 0; i++) {
                c = (list[mid * hashBytes + i] & 0xff) - (needle[i] & 0xff);
            }
            if (c == 0) {
                return true;
            }
            if (c < 0) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return false;
    }

    public int count() {
        return count;
    }

    /** More sessions were revoked than fit: a miss proves nothing, use the feed. */
    public boolean truncated() {
        return truncated;
    }

    public long issuedAt() {
        return issuedAt;
    }

    public long expiresAt() {
        return expiresAt;
    }

    public long since() {
        return since;
    }

    /**
     * Verify the signed list.
     *
     * @param jwks   the instance JWKS document ({@code {keys: [...]}})
     * @param issuer the issuer the list must carry (null skips the check; strongly discouraged)
     * @param nowMs  epoch ms
     */
    public static StatusList verify(String token, JsonNode jwks, String issuer, long nowMs, long skewSeconds) {
        String[] parts = token.split("\\.", -1);
        if (parts.length != 3 || token.length() > 4_000_000) {
            throw new StatusListException("malformed");
        }
        JsonNode header = parseJson(b64(parts[0]));
        if (!"statuslist+jwt".equals(header.path("typ").asText())) {
            throw new StatusListException("unsupported");
        }
        String alg = header.path("alg").asText("");
        String kty = switch (alg) {
            case "RS256", "PS256" -> "RSA";
            case "ES256", "ES384" -> "EC";
            case "EdDSA" -> "OKP";
            default -> throw new StatusListException("unsupported");
        };
        String crv = switch (alg) {
            case "ES256" -> "P-256";
            case "ES384" -> "P-384";
            case "EdDSA" -> "Ed25519";
            default -> null;
        };
        String kid = header.hasNonNull("kid") ? header.get("kid").asText() : null;
        JsonNode jwk = null;
        for (JsonNode k : jwks == null ? JSON.createArrayNode() : jwks.path("keys")) {
            if ((kid == null || kid.equals(k.path("kid").asText(null))) && kty.equals(k.path("kty").asText())
                    && (crv == null || crv.equals(k.path("crv").asText()))) {
                jwk = k;
                break;
            }
        }
        if (jwk == null) {
            throw new StatusListException("no_key");
        }
        boolean valid;
        try {
            PublicKey key = publicKey(jwk, kty);
            Signature s = signature(alg);
            s.initVerify(key);
            s.update((parts[0] + "." + parts[1]).getBytes(StandardCharsets.US_ASCII));
            valid = s.verify(b64(parts[2]));
        } catch (GeneralSecurityException | com.nimbusds.jose.JOSEException | java.text.ParseException | RuntimeException e) {
            throw new StatusListException("bad_signature");
        }
        if (!valid) {
            throw new StatusListException("bad_signature");
        }
        JsonNode claims = parseJson(b64(parts[1]));
        double nowSec = nowMs / 1000.0;
        if (!"atlas:session-status-list".equals(claims.path("aud").asText()) || !"session_status_list".equals(claims.path("token_use").asText())) {
            throw new StatusListException("bad_claims");
        }
        if (issuer != null && !issuer.equals(claims.path("iss").asText())) {
            throw new StatusListException("bad_claims");
        }
        long exp = num(claims, "exp"), iat = num(claims, "iat");
        if (exp + skewSeconds < nowSec) {
            throw new StatusListException("expired");
        }
        if (iat - skewSeconds > nowSec) {
            throw new StatusListException("bad_claims");
        }
        long hashBytes = num(claims, "hash_bytes");
        if (hashBytes < 4 || hashBytes > 32) {
            throw new StatusListException("bad_claims");
        }
        long count = num(claims, "count");
        if (!claims.path("h").isTextual()) {
            throw new StatusListException("bad_claims");
        }
        byte[] list = b64(claims.get("h").asText());
        if (count < 0 || list.length != count * hashBytes) {
            throw new StatusListException("bad_claims");
        }
        return new StatusList(list, (int) hashBytes, (int) count, claims.path("truncated").asBoolean(false), iat * 1000, exp * 1000, num(claims, "since"));
    }

    private static Signature signature(String alg) throws GeneralSecurityException {
        switch (alg) {
            case "RS256":
                return Signature.getInstance("SHA256withRSA");
            case "PS256": {
                Signature s = Signature.getInstance("RSASSA-PSS");
                s.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
                return s;
            }
            case "ES256":
                return Signature.getInstance("SHA256withECDSAinP1363Format");
            case "ES384":
                return Signature.getInstance("SHA384withECDSAinP1363Format");
            default:
                return Signature.getInstance("Ed25519");
        }
    }

    private static PublicKey publicKey(JsonNode jwk, String kty) throws java.text.ParseException, GeneralSecurityException, com.nimbusds.jose.JOSEException {
        JWK parsed = JWK.parse(jwk.toString());
        if (parsed instanceof RSAKey r) {
            return r.toRSAPublicKey();
        }
        if (parsed instanceof ECKey e) {
            return e.toECPublicKey();
        }
        if (parsed instanceof OctetKeyPair o) {
            byte[] prefix = {0x30, 0x2a, 0x30, 0x05, 0x06, 0x03, 0x2b, 0x65, 0x70, 0x03, 0x21, 0x00};
            byte[] x = o.getDecodedX();
            byte[] der = Arrays.copyOf(prefix, prefix.length + x.length);
            System.arraycopy(x, 0, der, prefix.length, x.length);
            return KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(der));
        }
        throw new GeneralSecurityException("unsupported key " + kty);
    }

    private static long num(JsonNode claims, String name) {
        JsonNode v = claims.path(name);
        if (!v.isNumber()) {
            throw new StatusListException("bad_claims");
        }
        return v.asLong();
    }

    private static byte[] b64(String s) {
        if (!s.matches("^[A-Za-z0-9_-]*$")) {
            throw new StatusListException("malformed");
        }
        return Base64.getUrlDecoder().decode(s);
    }

    private static JsonNode parseJson(byte[] bytes) {
        try {
            JsonNode n = JSON.readTree(bytes);
            if (n != null && n.isObject()) {
                return n;
            }
        } catch (IOException ignored) {
            // fall through
        }
        throw new StatusListException("malformed");
    }

    private static byte[] sha256(byte[] in) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(in);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    static List<String> algorithms() {
        return List.of("RS256", "PS256", "ES256", "ES384", "EdDSA");
    }
}
