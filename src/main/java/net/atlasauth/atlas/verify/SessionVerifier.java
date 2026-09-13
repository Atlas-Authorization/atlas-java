package net.atlasauth.atlas.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;

/**
 * §7.3 session-token verification for customer backends — the Java peer of the
 * TypeScript {@code AtlasBackend}.
 *
 * <p>The design constraint that matters is what this does <em>not</em> do: it
 * never calls Atlas on the hot path. Verification is local, against cached JWKS,
 * and the revocation window is bounded by the ~60s token lifetime instead.
 * {@link #verifyOnline(String)} exists for the handful of operations where 60
 * seconds is too long, and is the documented slow path.
 *
 * <pre>{@code
 * SessionVerifier verifier = SessionVerifier.builder()
 *     .jwksUrl("https://api.atlas.dev/v1/jwks")
 *     .issuer("https://clerk.your-domain.com")
 *     .build();
 *
 * VerifyResult result = verifier.verify(token);
 * if (result.ok()) {
 *     result.protect(new ProtectCondition().permission("org:reports:read"));
 * }
 * }</pre>
 */
public final class SessionVerifier {

    /** §7.3: five seconds either side, matching the server's minting tolerance. */
    public static final long CLOCK_SKEW_SECONDS = 5;

    private final String issuer;
    private final List<String> authorizedParties;
    private final JwksCache jwks;
    private final LongSupplier now;
    private final HttpClient http;
    private final String secretKey;
    private final String bapiBaseUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    private SessionVerifier(Builder b) {
        if (b.jwksUrl == null || b.jwksUrl.isEmpty()) {
            throw new IllegalArgumentException("SessionVerifier requires a jwksUrl.");
        }
        if (b.issuer == null || b.issuer.isEmpty()) {
            throw new IllegalArgumentException("SessionVerifier requires an issuer — an unchecked issuer accepts any Atlas instance.");
        }
        this.issuer = b.issuer;
        this.authorizedParties = b.authorizedParties;
        this.now = b.now != null ? b.now : System::currentTimeMillis;
        this.http = b.http != null ? b.http : HttpClient.newHttpClient();
        this.secretKey = b.secretKey;
        this.bapiBaseUrl = b.bapiBaseUrl;
        this.jwks = new JwksCache(b.jwksUrl, this.http, this.now);
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Verify locally. No network call unless the {@code kid} is unknown, and at most one a minute. */
    public VerifyResult verify(String token) {
        if (token == null || token.split("\\.").length != 3) {
            return VerifyResult.failed(VerifyResult.Reason.MALFORMED);
        }

        SignedJWT jwt;
        try {
            jwt = SignedJWT.parse(token);
        } catch (Exception e) {
            return VerifyResult.failed(VerifyResult.Reason.MALFORMED);
        }

        String kid = jwt.getHeader().getKeyID();
        JWKSet keys = jwks.get(kid);
        if (keys == null || keys.getKeys().isEmpty()) {
            return VerifyResult.failed(VerifyResult.Reason.NO_KEYS);
        }

        try {
            // Pinned to RS256. Without this a token whose header says alg:none — or
            // any algorithm the key material can be coerced into — would verify.
            if (!JWSAlgorithm.RS256.equals(jwt.getHeader().getAlgorithm())) {
                return VerifyResult.failed(VerifyResult.Reason.INVALID);
            }
            if (!verifySignature(jwt, keys, kid)) {
                return VerifyResult.failed(VerifyResult.Reason.INVALID);
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            long nowSec = now.getAsLong() / 1000L;

            if (!issuer.equals(claims.getIssuer())) {
                return VerifyResult.failed(VerifyResult.Reason.INVALID);
            }
            Date exp = claims.getExpirationTime();
            if (exp == null || nowSec > (exp.getTime() / 1000L) + CLOCK_SKEW_SECONDS) {
                return VerifyResult.failed(VerifyResult.Reason.INVALID);
            }
            Date nbf = claims.getNotBeforeTime();
            if (nbf != null && nowSec < (nbf.getTime() / 1000L) - CLOCK_SKEW_SECONDS) {
                return VerifyResult.failed(VerifyResult.Reason.INVALID);
            }

            Map<String, Object> all = claims.getClaims();

            // §13.1 token-confusion guard: reject an OP access/id token replayed as
            // a session. An absent token_use/aud is a valid (legacy) session.
            Object tokenUse = all.get("token_use");
            if ((tokenUse != null && !"session".equals(tokenUse)) || all.get("aud") != null) {
                return VerifyResult.failed(VerifyResult.Reason.INVALID);
            }

            if (authorizedParties != null && !authorizedParties.isEmpty()) {
                Object azp = all.get("azp");
                if (azp == null || !authorizedParties.contains(String.valueOf(azp))) {
                    return VerifyResult.failed(VerifyResult.Reason.UNAUTHORIZED_PARTY);
                }
            }

            return VerifyResult.authorized(new SessionClaims(all));
        } catch (Exception e) {
            return VerifyResult.failed(VerifyResult.Reason.INVALID);
        }
    }

    private boolean verifySignature(SignedJWT jwt, JWKSet keys, String kid) throws Exception {
        for (JWK key : keys.getKeys()) {
            if (kid != null && !kid.equals(key.getKeyID())) {
                continue;
            }
            if (!(key instanceof RSAKey rsaKey)) {
                continue;
            }
            JWSVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
            if (jwt.verify(verifier)) {
                return true;
            }
        }
        return false;
    }

    /**
     * §7.3 the documented slow path: ask Atlas whether the session is still live.
     * Fails closed — the caller reached for this precisely because a stale answer
     * was unacceptable, so an outage returns "invalid", never the local result.
     */
    public VerifyResult verifyOnline(String token) {
        VerifyResult local = verify(token);
        if (!local.ok()) {
            return local;
        }
        if (secretKey == null || bapiBaseUrl == null) {
            throw new IllegalStateException(
                    "verifyOnline needs secretKey and bapiBaseUrl. Without them it would silently fall back to local verification.");
        }
        try {
            String body = mapper.writeValueAsString(Map.of("token", token));
            HttpRequest request = HttpRequest.newBuilder(URI.create(bapiBaseUrl + "/v1/tokens/verify"))
                    .header("authorization", "Bearer " + secretKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return VerifyResult.failed(VerifyResult.Reason.INVALID);
            }
            Map<?, ?> parsed = mapper.readValue(response.body(), Map.class);
            return Boolean.TRUE.equals(parsed.get("verified"))
                    ? local
                    : VerifyResult.failed(VerifyResult.Reason.INVALID);
        } catch (Exception e) {
            return VerifyResult.failed(VerifyResult.Reason.INVALID);
        }
    }

    /**
     * Verify whatever a request carries: the {@code Authorization: Bearer} header
     * wins over a {@code __session} cookie, because a caller that set the header
     * deliberately should not be overridden by a stale cookie.
     */
    public VerifyResult authenticateRequest(Map<String, String> headers) {
        String authorization = header(headers, "authorization");
        String cookie = header(headers, "cookie");
        String bearer = authorization != null && authorization.startsWith("Bearer ")
                ? authorization.substring(7) : null;
        String fromCookie = cookie != null ? readCookie(cookie, "__session") : null;
        String token = bearer != null ? bearer : fromCookie;
        if (token == null) {
            return VerifyResult.failed(VerifyResult.Reason.MALFORMED);
        }
        return verify(token);
    }

    private static String header(Map<String, String> headers, String name) {
        if (headers == null) {
            return null;
        }
        String v = headers.get(name);
        if (v != null) {
            return v;
        }
        for (Map.Entry<String, String> e : headers.entrySet()) {
            if (e.getKey() != null && e.getKey().equalsIgnoreCase(name)) {
                return e.getValue();
            }
        }
        return null;
    }

    private static String readCookie(String header, String name) {
        for (String part : header.split(";")) {
            String trimmed = part.trim();
            int eq = trimmed.indexOf('=');
            if (eq > 0 && trimmed.substring(0, eq).equals(name)) {
                return trimmed.substring(eq + 1);
            }
        }
        return null;
    }

    // ---- convenience static authz helpers, twins of the bound result.has/protect ----

    public static boolean hasPermission(SessionClaims claims, String permission) {
        return Authz.has(claims, new ProtectCondition().permission(permission));
    }

    public static boolean hasRole(SessionClaims claims, String role) {
        return Authz.has(claims, new ProtectCondition().role(role));
    }

    public static boolean has(SessionClaims claims, ProtectCondition condition) {
        return Authz.has(claims, condition);
    }

    public static SessionClaims protect(SessionClaims claims, ProtectCondition condition) {
        return Authz.protect(claims, condition);
    }

    public static final class Builder {
        private String jwksUrl;
        private String issuer;
        private List<String> authorizedParties;
        private LongSupplier now;
        private HttpClient http;
        private String secretKey;
        private String bapiBaseUrl;

        public Builder jwksUrl(String jwksUrl) {
            this.jwksUrl = jwksUrl;
            return this;
        }

        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder authorizedParties(Collection<String> parties) {
            this.authorizedParties = parties == null ? null : List.copyOf(parties);
            return this;
        }

        /** For {@link SessionVerifier#verifyOnline(String)}. */
        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        /** For {@link SessionVerifier#verifyOnline(String)}. */
        public Builder bapiBaseUrl(String bapiBaseUrl) {
            this.bapiBaseUrl = bapiBaseUrl;
            return this;
        }

        public Builder httpClient(HttpClient http) {
            this.http = http;
            return this;
        }

        /** Injectable clock (epoch millis), for tests. */
        public Builder now(LongSupplier now) {
            this.now = now;
            return this;
        }

        public SessionVerifier build() {
            return new SessionVerifier(this);
        }
    }
}
