package net.atlasauth.atlas.verify;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Verifies Atlas-issued OAuth access tokens for your own API (an OAuth 2.0 resource server),
 * locally against the instance JWKS — RS256 or ES256, nothing else: the issuer, expiry, that it IS
 * an access token, that its {@code aud} names your API (RFC 8707), and the scopes a route needs. A
 * refusal carries the RFC 6750 / RFC 9728 {@code WWW-Authenticate} challenge, so an OAuth or MCP
 * client can discover Atlas from the 401 ({@code resource_metadata}) or ask for the missing scope.
 *
 * <p>DPoP-bound tokens ({@code cnf.jkt}) are refused: this verifier checks Bearer tokens only.
 */
public final class AccessTokenVerifier {

    /** The verdict; {@link #ok} with claims on success, else status + challenge. */
    public record Result(boolean ok, int status, String error, String description, String wwwAuthenticate,
                         Map<String, Object> claims, List<String> scopes, String subject, String clientId) {
    }

    private static final Pattern AUTHZ = Pattern.compile("^(Bearer|DPoP)\\s+([A-Za-z0-9\\-._~+/]+=*)$",
            Pattern.CASE_INSENSITIVE);

    private final String issuer;
    private final List<String> audiences;
    private final List<String> requiredScopes;
    private final String resourceMetadataUrl;
    private final String realm;
    private final LongSupplier now;
    private final JwksCache jwks;

    private AccessTokenVerifier(Builder b) {
        if (b.issuer == null || b.issuer.isEmpty()) {
            throw new IllegalArgumentException("AccessTokenVerifier requires an issuer.");
        }
        if (!b.anyAudience && b.audiences.isEmpty()) {
            throw new IllegalArgumentException("AccessTokenVerifier requires an audience (or anyAudience()).");
        }
        this.issuer = b.issuer.replaceAll("/+$", "");
        this.audiences = b.anyAudience ? null : List.copyOf(b.audiences);
        this.requiredScopes = List.copyOf(b.requiredScopes);
        this.resourceMetadataUrl = b.resourceMetadataUrl;
        this.realm = b.realm;
        this.now = b.now != null ? b.now : System::currentTimeMillis;
        HttpClient http = b.http != null ? b.http : HttpClient.newHttpClient();
        String url = b.jwksUrl != null ? b.jwksUrl : this.issuer + "/.well-known/jwks.json";
        this.jwks = new JwksCache(url, http, this.now);
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Verify an {@code Authorization} header value. Never throws for a bad token. */
    public Result verify(String authorization, String... extraScopes) {
        List<String> required = new ArrayList<>(requiredScopes);
        required.addAll(Arrays.asList(extraScopes));
        String value = authorization == null ? "" : authorization.trim();
        if (value.isEmpty()) {
            return refuse(401, null, "Authentication required.", required);
        }
        Matcher m = AUTHZ.matcher(value);
        if (!m.matches()) {
            return refuse(400, "invalid_request", "Malformed Authorization header.", required);
        }
        if (m.group(1).equalsIgnoreCase("dpop")) {
            return refuse(401, "invalid_token", "DPoP-bound access tokens are not accepted here.", required);
        }
        String token = m.group(2);
        String invalid = "The access token is invalid or expired.";
        SignedJWT jwt;
        try {
            jwt = SignedJWT.parse(token);
        } catch (Exception e) {
            return refuse(401, "invalid_token", "The access token is malformed.", required);
        }
        String kid = jwt.getHeader().getKeyID();
        JWKSet keys = jwks.get(kid);
        if (keys == null || keys.getKeys().isEmpty()) {
            return refuse(401, "invalid_token", "No signing keys are available to verify the token.", required);
        }
        Map<String, Object> claims;
        try {
            if (!SigningAlgs.allowed(jwt) || !SigningAlgs.verify(jwt, keys, kid)) {
                return refuse(401, "invalid_token", invalid, required);
            }
            JWTClaimsSet set = jwt.getJWTClaimsSet();
            long nowSec = now.getAsLong() / 1000L;
            if (!issuer.equals(set.getIssuer())) {
                return refuse(401, "invalid_token", invalid, required);
            }
            Date exp = set.getExpirationTime();
            if (exp == null || nowSec > exp.getTime() / 1000L + SessionVerifier.CLOCK_SKEW_SECONDS) {
                return refuse(401, "invalid_token", invalid, required);
            }
            Date nbf = set.getNotBeforeTime();
            if (nbf != null && nowSec < nbf.getTime() / 1000L - SessionVerifier.CLOCK_SKEW_SECONDS) {
                return refuse(401, "invalid_token", invalid, required);
            }
            claims = set.getClaims();
            Object tokenUse = claims.get("token_use");
            if (tokenUse != null && !"access_token".equals(tokenUse)) {
                return refuse(401, "invalid_token", "The token is not an access token.", required);
            }
            if (audiences != null) {
                List<String> aud = set.getAudience();
                if (aud == null || aud.stream().noneMatch(audiences::contains)) {
                    return refuse(401, "invalid_token", "The access token was not issued for this resource.", required);
                }
            }
        } catch (Exception e) {
            return refuse(401, "invalid_token", invalid, required);
        }
        if (claims.get("cnf") instanceof Map<?, ?> cnf && cnf.containsKey("jkt")) {
            return refuse(401, "invalid_token", "DPoP-bound access tokens are not accepted here.", required);
        }
        List<String> scopes = claims.get("scope") instanceof String s && !s.isBlank()
                ? List.of(s.trim().split("\\s+")) : List.of();
        Set<String> granted = new LinkedHashSet<>(scopes);
        if (claims.get("permissions") instanceof List<?> perms) {
            for (Object p : perms) {
                if (p instanceof String ps) {
                    granted.add(ps);
                }
            }
        }
        List<String> missing = required.stream().filter(s -> !granted.contains(s)).toList();
        if (!missing.isEmpty()) {
            return refuse(403, "insufficient_scope",
                    "The access token is missing the required scope(s): " + String.join(" ", missing) + ".", required);
        }
        Object clientId = claims.get("client_id") != null ? claims.get("client_id") : claims.get("azp");
        Object sub = claims.get("sub");
        return new Result(true, 200, null, "", "", Collections.unmodifiableMap(new LinkedHashMap<>(claims)), scopes,
                sub == null ? "" : String.valueOf(sub), clientId == null ? null : String.valueOf(clientId));
    }

    private Result refuse(int status, String error, String description, List<String> required) {
        List<String> scope = (error == null || error.equals("insufficient_scope")) ? required : null;
        return new Result(false, status, error, description,
                buildWwwAuthenticate(realm, error, description, scope, resourceMetadataUrl), Map.of(), List.of(), "", null);
    }

    /**
     * An RFC 6750 / RFC 9728 Bearer challenge. Without {@code error} it is the bare "authentication
     * required" challenge (no token was sent). Every argument is nullable.
     */
    public static String buildWwwAuthenticate(String realm, String error, String description, List<String> scope,
                                              String resourceMetadataUrl) {
        List<String> parts = new ArrayList<>();
        if (realm != null) {
            parts.add("realm=" + quote(realm));
        }
        if (error != null) {
            parts.add("error=" + quote(error));
            if (description != null) {
                parts.add("error_description=" + quote(description));
            }
        }
        if (scope != null && !scope.isEmpty()) {
            parts.add("scope=" + quote(String.join(" ", scope)));
        }
        if (resourceMetadataUrl != null) {
            parts.add("resource_metadata=" + quote(resourceMetadataUrl));
        }
        return parts.isEmpty() ? "Bearer" : "Bearer " + String.join(", ", parts);
    }

    /** The RFC 9728 protected-resource metadata document for your API. */
    public static Map<String, Object> protectedResourceMetadata(String resource, String issuer, List<String> scopesSupported) {
        Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("resource", resource);
        doc.put("authorization_servers", List.of(issuer.replaceAll("/+$", "")));
        doc.put("bearer_methods_supported", List.of("header"));
        if (scopesSupported != null && !scopesSupported.isEmpty()) {
            doc.put("scopes_supported", scopesSupported);
        }
        return doc;
    }

    /** The RFC 9728 well-known path for a resource identifier. */
    public static String protectedResourceMetadataPath(String resource) {
        String path = URI.create(resource).getPath();
        return "/.well-known/oauth-protected-resource" + (path == null ? "" : path.replaceAll("/+$", ""));
    }

    private static String quote(String v) {
        return "\"" + v.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    /** Builder for {@link AccessTokenVerifier}. */
    public static final class Builder {
        private String issuer;
        private final List<String> audiences = new ArrayList<>();
        private boolean anyAudience;
        private final List<String> requiredScopes = new ArrayList<>();
        private String jwksUrl;
        private String resourceMetadataUrl;
        private String realm;
        private HttpClient http;
        private LongSupplier now;

        public Builder issuer(String v) { this.issuer = v; return this; }
        /** Your API identifier (the resource server identifier); call again to accept several. */
        public Builder audience(String v) { this.audiences.add(v); return this; }
        /** Skip the audience check (userinfo-style tokens). */
        public Builder anyAudience() { this.anyAudience = true; return this; }
        public Builder requiredScopes(String... v) { this.requiredScopes.addAll(Arrays.asList(v)); return this; }
        public Builder jwksUrl(String v) { this.jwksUrl = v; return this; }
        public Builder resourceMetadataUrl(String v) { this.resourceMetadataUrl = v; return this; }
        public Builder realm(String v) { this.realm = v; return this; }
        public Builder httpClient(HttpClient v) { this.http = v; return this; }
        public Builder now(LongSupplier v) { this.now = v; return this; }

        public AccessTokenVerifier build() {
            return new AccessTokenVerifier(this);
        }
    }
}
