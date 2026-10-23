package net.atlasauth.atlas.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;

/**
 * Verifies {@link LegacyIssuer} tokens for {@link SessionVerifier}: Better Auth {@code jwt} plugin tokens
 * (EdDSA / Ed25519, ES256, ES512, RS256, PS256) during a cutover, against each issuer's own keys only.
 * Ed25519 is verified with the JDK's own provider; the others with Nimbus.
 */
public final class LegacyIssuerVerifier {

    /** How long a resolved sub → Atlas id mapping is reused. */
    public static final long SUBJECT_TTL_MS = 300_000L;
    /** Cap on cached subjects, so a stream of distinct tokens cannot grow memory unbounded. */
    public static final int SUBJECT_CACHE_MAX = 10_000;

    /** Outcome of {@link #verify}. */
    public record Result(String outcome, Map<String, Object> claims) {
        public boolean ok() {
            return "ok".equals(outcome);
        }
    }

    private static final byte[] ED25519_SPKI_PREFIX = {
            0x30, 0x2a, 0x30, 0x05, 0x06, 0x03, 0x2b, 0x65, 0x70, 0x03, 0x21, 0x00};

    private record CachedSubject(String atlasId, long at) {
    }

    private final List<LegacyIssuer> issuers;
    private final Map<String, JwksCache> caches = new HashMap<>();
    private final Map<String, JWKSet> inline = new HashMap<>();
    private final Map<String, CachedSubject> subjects = new LinkedHashMap<>();
    private final HttpClient http;
    private final String secretKey;
    private final String apiUrl;
    private final LongSupplier now;
    private final long leeway;
    private final ObjectMapper mapper = new ObjectMapper();

    public LegacyIssuerVerifier(List<LegacyIssuer> issuers, HttpClient http, String secretKey, String apiUrl,
                                LongSupplier now, long leewaySeconds) {
        this.issuers = List.copyOf(issuers);
        this.http = http;
        this.secretKey = secretKey;
        this.apiUrl = (apiUrl != null && !apiUrl.isEmpty() ? apiUrl : "https://api.atlasauth.net").replaceAll("/+$", "");
        this.now = now != null ? now : System::currentTimeMillis;
        this.leeway = leewaySeconds;
        for (LegacyIssuer entry : this.issuers) {
            if (entry.resolveSubject() == null && (secretKey == null || secretKey.isEmpty())) {
                throw new IllegalArgumentException("legacyIssuers: \"" + entry.issuer() + "\" maps its sub to an Atlas user "
                        + "through the Backend API, which needs secretKey on the verifier, or give the issuer a resolveSubject.");
            }
            if (entry.jwksUrl() != null) {
                caches.put(entry.issuer(), new JwksCache(entry.jwksUrl(), http, this.now));
            } else {
                try {
                    inline.put(entry.issuer(), JWKSet.parse(entry.jwks()));
                } catch (java.text.ParseException e) {
                    throw new IllegalArgumentException("legacyIssuers: \"" + entry.issuer() + "\" has an unreadable jwks: "
                            + e.getMessage(), e);
                }
            }
        }
    }

    /** The UNVERIFIED {@code iss} of a compact JWT, for routing only. */
    public static String peekIssuer(String token) {
        if (token == null) {
            return null;
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }
        try {
            JsonNode payload = new ObjectMapper().readTree(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode iss = payload.get("iss");
            return iss != null && iss.isTextual() ? iss.asText() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /** The configured entry for this {@code iss}, when its window is still open. */
    public LegacyIssuer match(String iss) {
        if (iss == null || iss.isEmpty()) {
            return null;
        }
        for (LegacyIssuer entry : issuers) {
            if (entry.issuer().equals(iss)) {
                return entry.untilMs() > now.getAsLong() ? entry : null;
            }
        }
        return null;
    }

    /** Verify one token for a matched entry: outcome {@code ok}, {@code no_keys} or {@code invalid}. */
    public Result verify(String token, LegacyIssuer entry) {
        SignedJWT jwt;
        Map<String, Object> payload;
        try {
            jwt = SignedJWT.parse(token);
            payload = jwt.getJWTClaimsSet().getClaims();
        } catch (Exception e) {
            return new Result("invalid", null);
        }
        JWSAlgorithm alg = jwt.getHeader().getAlgorithm();
        String algName = alg == null ? null : alg.getName();
        String kid = jwt.getHeader().getKeyID();
        if (algName == null || !entry.algorithms().contains(algName)) {
            return new Result("invalid", null);
        }
        JWKSet keys = keys(entry, kid);
        if (keys == null || keys.getKeys().isEmpty()) {
            return new Result("no_keys", null);
        }
        boolean verified = false;
        for (JWK key : keys.getKeys()) {
            if (kid != null && !kid.equals(key.getKeyID())) {
                continue;
            }
            if (key.getAlgorithm() != null && !key.getAlgorithm().getName().equals(algName)) {
                continue;
            }
            if (verifySignature(jwt, algName, key)) {
                verified = true;
                break;
            }
        }
        if (!verified) {
            return new Result("invalid", null);
        }

        long nowSec = now.getAsLong() / 1000L;
        if (!entry.issuer().equals(payload.get("iss"))) {
            return new Result("invalid", null);
        }
        // A legacy token without an expiry would outlive the window it is accepted in.
        Long exp = epochSeconds(payload.get("exp"));
        if (exp == null || nowSec > exp + leeway) {
            return new Result("invalid", null);
        }
        Long nbf = epochSeconds(payload.get("nbf"));
        if (nbf != null && nowSec < nbf - leeway) {
            return new Result("invalid", null);
        }
        if (entry.audience() != null) {
            Object aud = payload.get("aud");
            List<?> tokenAud = aud instanceof List<?> l ? l : aud instanceof String s ? List.of(s) : List.of();
            boolean any = false;
            for (String expected : entry.audience()) {
                if (tokenAud.contains(expected)) {
                    any = true;
                    break;
                }
            }
            if (!any) {
                return new Result("invalid", null);
            }
        }
        String legacySub = payload.get("sub") instanceof String s && !s.isEmpty() ? s : null;
        if (legacySub == null && payload.get("id") instanceof String id && !id.isEmpty()) {
            legacySub = id;
        }
        if (legacySub == null) {
            return new Result("invalid", null);
        }
        Map<String, Object> plain = new LinkedHashMap<>();
        for (Map.Entry<String, Object> e : payload.entrySet()) {
            Object v = e.getValue();
            plain.put(e.getKey(), v instanceof java.util.Date d ? d.getTime() / 1000L : v);
        }
        String atlasId = resolve(legacySub, plain, entry);
        if (atlasId == null || atlasId.isEmpty()) {
            return new Result("invalid", null);
        }
        Map<String, Object> claims = new LinkedHashMap<>(plain);
        claims.put("sub", atlasId);
        claims.put("sid", plain.get("sid") instanceof String sid ? sid : "");
        claims.put("external_id", legacySub);
        Map<String, Object> legacy = new LinkedHashMap<>();
        legacy.put("kind", entry.kind());
        legacy.put("issuer", entry.issuer());
        legacy.put("sub", legacySub);
        claims.put("legacy", legacy);
        return new Result("ok", claims);
    }

    private static Long epochSeconds(Object v) {
        if (v instanceof java.util.Date d) {
            return d.getTime() / 1000L;
        }
        if (v instanceof Number n) {
            return n.longValue();
        }
        return null;
    }

    private JWKSet keys(LegacyIssuer entry, String kid) {
        JwksCache cache = caches.get(entry.issuer());
        return cache != null ? cache.get(kid) : inline.get(entry.issuer());
    }

    /** Verify the signature of {@code jwt} with {@code key} for {@code alg}, the key's type deciding what it may verify. */
    static boolean verifySignature(SignedJWT jwt, String alg, JWK key) {
        try {
            switch (alg) {
                case "RS256", "PS256" -> {
                    return key instanceof RSAKey rsa && jwt.verify(new RSASSAVerifier(rsa.toRSAPublicKey()));
                }
                case "ES256" -> {
                    return key instanceof ECKey ec && Curve.P_256.equals(ec.getCurve())
                            && jwt.verify(new ECDSAVerifier(ec.toECPublicKey()));
                }
                case "ES512" -> {
                    return key instanceof ECKey ec && Curve.P_521.equals(ec.getCurve())
                            && jwt.verify(new ECDSAVerifier(ec.toECPublicKey()));
                }
                case "EdDSA" -> {
                    if (!(key instanceof OctetKeyPair okp) || !Curve.Ed25519.equals(okp.getCurve())) {
                        return false;
                    }
                    byte[] x = okp.getX().decode();
                    if (x.length != 32) {
                        return false;
                    }
                    byte[] spki = new byte[ED25519_SPKI_PREFIX.length + 32];
                    System.arraycopy(ED25519_SPKI_PREFIX, 0, spki, 0, ED25519_SPKI_PREFIX.length);
                    System.arraycopy(x, 0, spki, ED25519_SPKI_PREFIX.length, 32);
                    PublicKey pub = KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(spki));
                    Signature sig = Signature.getInstance("Ed25519");
                    sig.initVerify(pub);
                    sig.update(jwt.getSigningInput());
                    return sig.verify(jwt.getSignature().decode());
                }
                default -> {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    private String resolve(String legacySub, Map<String, Object> claims, LegacyIssuer entry) {
        if (entry.resolveSubject() != null) {
            try {
                String id = entry.resolveSubject().apply(legacySub, claims);
                return id != null && !id.isEmpty() ? id : null;
            } catch (RuntimeException e) {
                return null;
            }
        }
        String key = entry.issuer() + "\0" + legacySub;
        long t = now.getAsLong();
        synchronized (subjects) {
            CachedSubject hit = subjects.get(key);
            if (hit != null && t - hit.at() < SUBJECT_TTL_MS) {
                return hit.atlasId();
            }
        }
        String found;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(apiUrl + "/v1/users?external_id="
                            + URLEncoder.encode(legacySub, StandardCharsets.UTF_8) + "&limit=1"))
                    .timeout(Duration.ofSeconds(10))
                    .header("authorization", "Bearer " + secretKey)
                    .header("accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return null; // not cached: a transient failure should not stick
            }
            JsonNode data = mapper.readTree(response.body()).path("data");
            JsonNode id = data.isArray() && data.size() > 0 ? data.get(0).get("id") : null;
            found = id != null && id.isTextual() && !id.asText().isEmpty() ? id.asText() : null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            return null;
        }
        synchronized (subjects) {
            if (subjects.size() >= SUBJECT_CACHE_MAX) {
                subjects.clear();
            }
            subjects.put(key, new CachedSubject(found, t));
        }
        return found;
    }
}
