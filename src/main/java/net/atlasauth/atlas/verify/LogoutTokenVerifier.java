package net.atlasauth.atlas.verify;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;

/**
 * Verifies an OIDC Back-Channel Logout {@code logout_token} (OpenID Connect
 * Back-Channel Logout 1.0 §2.6) that Atlas POSTs to your app's
 * {@code backchannel_logout_uri} when a user signs out.
 *
 * <p>Checks, in order: RS256 signature against the instance JWKS (cached, like
 * {@link SessionVerifier}); header {@code typ} is {@code logout+jwt} when present;
 * {@code iss} equals the issuer; {@code aud} contains your client id; {@code iat}
 * is present and not in the future; {@code exp} (when present) has not passed;
 * {@code jti} is present; {@code events} contains
 * {@value #BACKCHANNEL_LOGOUT_EVENT} with an object value; {@code sid} and/or
 * {@code sub} is present; and there is no {@code nonce}.
 *
 * <pre>{@code
 * LogoutTokenVerifier verifier = LogoutTokenVerifier.builder()
 *     .jwksUrl("https://api.atlasauth.net/v1/jwks")
 *     .issuer("https://atlasauth.net")
 *     .clientId("atlas_client_123")
 *     .build();
 *
 * try {
 *     LogoutTokenClaims claims = verifier.verify(logoutToken);
 *     sessions.endBySid(claims.sid());
 * } catch (LogoutTokenException e) {
 *     // 503 + Retry-After makes Atlas retry (keys unreachable); 400 is final.
 *     response.setStatus(e.retryable() ? 503 : 400);
 * }
 * }</pre>
 *
 * <p>The verifier does not remember tokens. To reject a replayed token, record each
 * {@link LogoutTokenClaims#jti()} until the token would have expired and refuse one
 * you have already seen.
 */
public final class LogoutTokenVerifier {

    /** The event key that marks a JWT as a back-channel logout token. */
    public static final String BACKCHANNEL_LOGOUT_EVENT = "http://schemas.openid.net/event/backchannel-logout";

    /** Default tolerance, in seconds, for clock differences on {@code iat} and {@code exp}. */
    public static final long DEFAULT_CLOCK_SKEW_SECONDS = 60;

    private final String issuer;
    private final String clientId;
    private final long clockSkewSeconds;
    private final JwksCache jwks;
    private final LongSupplier now;

    private LogoutTokenVerifier(Builder b) {
        if (b.jwksUrl == null || b.jwksUrl.isEmpty()) {
            throw new IllegalArgumentException("LogoutTokenVerifier requires a jwksUrl.");
        }
        if (b.issuer == null || b.issuer.isEmpty()) {
            throw new IllegalArgumentException("LogoutTokenVerifier requires an issuer.");
        }
        if (b.clientId == null || b.clientId.isEmpty()) {
            throw new IllegalArgumentException("LogoutTokenVerifier requires your clientId (the token audience).");
        }
        this.issuer = b.issuer;
        this.clientId = b.clientId;
        this.clockSkewSeconds = b.clockSkewSeconds;
        this.now = b.now != null ? b.now : System::currentTimeMillis;
        HttpClient http = b.http != null ? b.http : HttpClient.newHttpClient();
        this.jwks = new JwksCache(b.jwksUrl, http, this.now);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Read {@code logout_token} out of an {@code application/x-www-form-urlencoded}
     * request body and verify it.
     *
     * @throws LogoutTokenException when the field is missing or the token is invalid
     */
    public LogoutTokenClaims verifyFormBody(String formBody) {
        return verify(extractLogoutToken(formBody));
    }

    /** The {@code logout_token} field of a form-encoded body, or {@code null} when absent. */
    public static String extractLogoutToken(String formBody) {
        if (formBody == null) {
            return null;
        }
        for (String pair : formBody.split("&")) {
            int eq = pair.indexOf('=');
            String key = URLDecoder.decode(eq < 0 ? pair : pair.substring(0, eq), StandardCharsets.UTF_8);
            if ("logout_token".equals(key)) {
                return eq < 0 ? "" : URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    /**
     * Verify a logout token.
     *
     * @return the claims you need to end the matching local session(s)
     * @throws LogoutTokenException with a {@link LogoutTokenException.Reason} when it is not valid
     */
    public LogoutTokenClaims verify(String token) {
        if (token == null || token.isEmpty() || token.split("\\.").length != 3) {
            throw new LogoutTokenException(LogoutTokenException.Reason.MALFORMED, "logout_token is missing or not a JWT");
        }
        SignedJWT jwt;
        JWTClaimsSet claims;
        try {
            jwt = SignedJWT.parse(token);
            claims = jwt.getJWTClaimsSet();
        } catch (Exception e) {
            throw new LogoutTokenException(LogoutTokenException.Reason.MALFORMED, "logout_token could not be parsed");
        }

        if (!SigningAlgs.allowed(jwt)) {
            throw new LogoutTokenException(LogoutTokenException.Reason.BAD_ALGORITHM, "logout_token must be signed with RS256 or ES256");
        }
        JOSEObjectType typ = jwt.getHeader().getType();
        if (typ != null) {
            String t = typ.getType().toLowerCase(java.util.Locale.ROOT);
            if (!t.equals("logout+jwt") && !t.equals("application/logout+jwt")) {
                throw new LogoutTokenException(LogoutTokenException.Reason.WRONG_TYPE, "logout_token typ must be logout+jwt");
            }
        }

        String kid = jwt.getHeader().getKeyID();
        JWKSet keys = jwks.get(kid);
        if (keys == null || keys.getKeys().isEmpty()) {
            throw new LogoutTokenException(LogoutTokenException.Reason.NO_KEYS, "no signing keys available");
        }
        // A key Atlas just rotated to, whose refetch failed: temporary.
        if (kid != null && jwks.lastOutcome() == JwksCache.Outcome.FAILED && keys.getKeyByKeyId(kid) == null) {
            throw new LogoutTokenException(LogoutTokenException.Reason.NO_KEYS, "signing key " + kid + " could not be fetched");
        }
        if (!signatureValid(jwt, keys, kid)) {
            throw new LogoutTokenException(LogoutTokenException.Reason.BAD_SIGNATURE, "logout_token signature is invalid");
        }

        if (!issuer.equals(claims.getIssuer())) {
            throw new LogoutTokenException(LogoutTokenException.Reason.WRONG_ISSUER, "logout_token iss does not match");
        }
        List<String> aud = claims.getAudience();
        if (aud == null || !aud.contains(clientId)) {
            throw new LogoutTokenException(LogoutTokenException.Reason.WRONG_AUDIENCE, "logout_token aud does not include this client");
        }

        long nowSec = now.getAsLong() / 1000L;
        Date iat = claims.getIssueTime();
        if (iat == null) {
            throw new LogoutTokenException(LogoutTokenException.Reason.MISSING_IAT, "logout_token has no iat");
        }
        if (iat.getTime() / 1000L > nowSec + clockSkewSeconds) {
            throw new LogoutTokenException(LogoutTokenException.Reason.ISSUED_IN_FUTURE, "logout_token iat is in the future");
        }
        Date exp = claims.getExpirationTime();
        if (exp != null && nowSec > exp.getTime() / 1000L + clockSkewSeconds) {
            throw new LogoutTokenException(LogoutTokenException.Reason.EXPIRED, "logout_token has expired");
        }

        String jti = claims.getJWTID();
        if (jti == null || jti.isEmpty()) {
            throw new LogoutTokenException(LogoutTokenException.Reason.MISSING_JTI, "logout_token has no jti");
        }

        Map<String, Object> all = claims.getClaims();
        Object events = all.get("events");
        if (!(events instanceof Map<?, ?> eventMap) || !(eventMap.get(BACKCHANNEL_LOGOUT_EVENT) instanceof Map)) {
            throw new LogoutTokenException(LogoutTokenException.Reason.MISSING_EVENT,
                    "logout_token events must contain the back-channel logout event");
        }
        if (all.containsKey("nonce")) {
            throw new LogoutTokenException(LogoutTokenException.Reason.HAS_NONCE, "logout_token must not contain a nonce");
        }

        String sub = claims.getSubject();
        Object sidRaw = all.get("sid");
        String sid = sidRaw instanceof String s && !s.isEmpty() ? s : null;
        if ((sub == null || sub.isEmpty()) && sid == null) {
            throw new LogoutTokenException(LogoutTokenException.Reason.MISSING_SUBJECT, "logout_token needs sid or sub");
        }
        return new LogoutTokenClaims(sub, sid, jti, iat.getTime() / 1000L, all);
    }

    private static boolean signatureValid(SignedJWT jwt, JWKSet keys, String kid) {
        return SigningAlgs.verify(jwt, keys, kid);
    }

    /** Fluent builder. {@code jwksUrl}, {@code issuer} and {@code clientId} are required. */
    public static final class Builder {
        private String jwksUrl;
        private String issuer;
        private String clientId;
        private long clockSkewSeconds = DEFAULT_CLOCK_SKEW_SECONDS;
        private HttpClient http;
        private LongSupplier now;

        /** The instance JWKS URL (the same one {@link SessionVerifier} uses). */
        public Builder jwksUrl(String jwksUrl) {
            this.jwksUrl = jwksUrl;
            return this;
        }

        /** The issuer your app's ID tokens carry ({@code iss}). */
        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        /** Your app's client id — the logout token's audience. */
        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /** Tolerance for clock differences on {@code iat} and {@code exp}. Default 60 seconds. */
        public Builder clockSkewSeconds(long seconds) {
            this.clockSkewSeconds = seconds;
            return this;
        }

        public Builder httpClient(HttpClient http) {
            this.http = http;
            return this;
        }

        /** Override the clock (tests). Milliseconds since the epoch. */
        public Builder now(LongSupplier now) {
            this.now = now;
            return this;
        }

        public LogoutTokenVerifier build() {
            return new LogoutTokenVerifier(this);
        }
    }
}
