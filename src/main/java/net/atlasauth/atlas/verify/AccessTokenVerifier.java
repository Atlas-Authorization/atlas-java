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
 * <p>Sender-constrained tokens (RFC 9449 DPoP) are enforced: a token bound to a key
 * ({@code cnf.jkt}) is accepted only with the {@code DPoP} scheme and a valid DPoP proof from that
 * key, never as a plain Bearer token. {@link Builder#requireDpop} refuses unbound tokens
 * altogether, and {@link Builder#dpopNonces} demands a server nonce.
 */
public final class AccessTokenVerifier {

    /**
     * The verdict; {@link #ok} with claims on success, else status + challenge. {@code dpopNonce},
     * when not null, goes in the {@code DPoP-Nonce} response header (on success and refusal alike).
     */
    public record Result(boolean ok, int status, String error, String description, String wwwAuthenticate,
                         Map<String, Object> claims, List<String> scopes, String subject, String clientId,
                         boolean dpopBound, String dpopNonce) {
        /** The pre-DPoP shape (not bound, no nonce). */
        public Result(boolean ok, int status, String error, String description, String wwwAuthenticate,
                      Map<String, Object> claims, List<String> scopes, String subject, String clientId) {
            this(ok, status, error, description, wwwAuthenticate, claims, scopes, subject, clientId, false, null);
        }
    }

    /**
     * What verification reads from an HTTP request: the method, the URL (absolute, or a path resolved
     * against {@link Builder#publicOrigin}) for the DPoP {@code htu}, and the {@code Authorization}
     * and {@code DPoP} header values (either may be null).
     */
    public record ResourceRequest(String method, String url, String authorization, String dpop) {
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
    private final boolean requireDpop;
    private final String publicOrigin;
    private final DpopReplayStore dpopReplay;
    private final DpopNonceSource dpopNonces;
    private final Introspector introspector;

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
        this.requireDpop = b.requireDpop;
        this.publicOrigin = b.publicOrigin;
        this.dpopReplay = b.dpopReplay != null ? b.dpopReplay : new MemoryDpopReplayStore(10_000, this.now);
        this.dpopNonces = b.dpopNonces;
        this.introspector = b.introspector;
    }

    /** Whether a {@link Builder#publicOrigin} is set (framework adapters then pass the path, not the full URL). */
    public boolean hasPublicOrigin() {
        return publicOrigin != null;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Verify an {@code Authorization} header value with no DPoP proof: a DPoP-bound token is refused.
     * Use {@link #verifyRequest(ResourceRequest, String...)} for DPoP. Never throws for a bad token.
     */
    public Result verify(String authorization, String... extraScopes) {
        return verifyRequest(new ResourceRequest("GET", "", authorization, null), extraScopes);
    }

    /**
     * Verify a request: its {@code Authorization} header ({@code Bearer} or {@code DPoP} scheme) and,
     * for a DPoP-bound token, its {@code DPoP} proof against the method and URL. Never throws for a
     * bad token.
     */
    public Result verifyRequest(ResourceRequest request, String... extraScopes) {
        List<String> required = new ArrayList<>(requiredScopes);
        required.addAll(Arrays.asList(extraScopes));
        String nonce = dpopNonces == null ? null : dpopNonces.current();
        Refuser refuse = (status, error, description, req, scheme) ->
                refuse(status, error, description, req, scheme, nonce);
        String authorization = request.authorization();
        String value = authorization == null ? "" : authorization.trim();
        if (value.isEmpty()) {
            return refuse.at(401, null, "Authentication required.", required, null);
        }
        Matcher m = AUTHZ.matcher(value);
        if (!m.matches()) {
            return refuse.at(400, "invalid_request", "Malformed Authorization header.", required, null);
        }
        boolean presentedDpop = m.group(1).equalsIgnoreCase("dpop");
        String token = m.group(2);
        String invalid = "The access token is invalid or expired.";
        Map<String, Object> claims;
        boolean isJwt = token.split("\\.", -1).length == 3;
        if (!isJwt || (introspector != null && introspector.introspectJwts())) {
            // An opaque token (or a JWT the caller wants checked live) is validated by Atlas itself.
            if (introspector == null) {
                return refuse.at(401, "invalid_token",
                        "The access token is not a JWT; configure introspection to accept opaque tokens.", required, null);
            }
            Map<String, Object> active;
            try {
                active = introspector.introspect(token);
            } catch (Introspector.IntrospectionException e) {
                return refuse.at(401, "invalid_token", "The access token could not be validated right now.", required, null);
            }
            if (active == null) {
                return refuse.at(401, "invalid_token", invalid, required, null);
            }
            if (active.containsKey("iss") && !String.valueOf(active.get("iss")).replaceAll("/+$", "").equals(issuer)) {
                return refuse.at(401, "invalid_token", "The access token was issued by another authorization server.",
                        required, null);
            }
            if (active.get("exp") instanceof Number exp
                    && exp.longValue() + SessionVerifier.CLOCK_SKEW_SECONDS <= now.getAsLong() / 1000L) {
                return refuse.at(401, "invalid_token", invalid, required, null);
            }
            Object tokenUse = active.get("token_use");
            if (tokenUse != null && !"access_token".equals(tokenUse)) {
                return refuse.at(401, "invalid_token", "The token is not an access token.", required, null);
            }
            if (audiences != null) {
                Object aud = active.get("aud");
                List<?> auds = aud instanceof List<?> l ? l : aud == null ? List.of() : List.of(aud);
                if (auds.stream().noneMatch(a -> audiences.contains(String.valueOf(a)))) {
                    return refuse.at(401, "invalid_token", "The access token was not issued for this resource.", required, null);
                }
            }
            claims = active;
        } else {
            SignedJWT jwt;
            try {
                jwt = SignedJWT.parse(token);
            } catch (Exception e) {
                return refuse.at(401, "invalid_token", "The access token is malformed.", required, null);
            }
            String kid = jwt.getHeader().getKeyID();
            JWKSet keys = jwks.get(kid);
            if (keys == null || keys.getKeys().isEmpty()) {
                return refuse.at(401, "invalid_token", "No signing keys are available to verify the token.", required, null);
            }
            try {
                if (!SigningAlgs.allowed(jwt) || !SigningAlgs.verify(jwt, keys, kid)) {
                    return refuse.at(401, "invalid_token", invalid, required, null);
                }
                JWTClaimsSet set = jwt.getJWTClaimsSet();
                long nowSec = now.getAsLong() / 1000L;
                if (!issuer.equals(set.getIssuer())) {
                    return refuse.at(401, "invalid_token", invalid, required, null);
                }
                Date exp = set.getExpirationTime();
                if (exp == null || nowSec > exp.getTime() / 1000L + SessionVerifier.CLOCK_SKEW_SECONDS) {
                    return refuse.at(401, "invalid_token", invalid, required, null);
                }
                Date nbf = set.getNotBeforeTime();
                if (nbf != null && nowSec < nbf.getTime() / 1000L - SessionVerifier.CLOCK_SKEW_SECONDS) {
                    return refuse.at(401, "invalid_token", invalid, required, null);
                }
                claims = set.getClaims();
                Object tokenUse = claims.get("token_use");
                if (tokenUse != null && !"access_token".equals(tokenUse)) {
                    return refuse.at(401, "invalid_token", "The token is not an access token.", required, null);
                }
                if (audiences != null) {
                    List<String> aud = set.getAudience();
                    if (aud == null || aud.stream().noneMatch(audiences::contains)) {
                        return refuse.at(401, "invalid_token", "The access token was not issued for this resource.", required, null);
                    }
                }
            } catch (Exception e) {
                return refuse.at(401, "invalid_token", invalid, required, null);
            }
        }
        // Sender constraint (RFC 9449). A bound token is only ever a DPoP token.
        String jkt = null;
        if (claims.get("cnf") instanceof Map<?, ?> cnf && cnf.containsKey("jkt")) {
            if (!(cnf.get("jkt") instanceof String j) || j.isEmpty()) {
                return refuse.at(401, "invalid_token", invalid, required, null);
            }
            jkt = j;
        }
        if (jkt != null || requireDpop || presentedDpop) {
            if (jkt == null) {
                return refuse.at(401, "invalid_token", "This resource requires a DPoP-bound access token.", required, "DPoP");
            }
            if (!presentedDpop) {
                return refuse.at(401, "invalid_token", "A DPoP-bound access token must be sent with the DPoP scheme.",
                        required, "DPoP");
            }
            String proof = request.dpop() == null ? "" : request.dpop().trim();
            if (proof.isEmpty()) {
                return refuse.at(401, "invalid_dpop_proof", "A DPoP proof is required.", required, "DPoP");
            }
            String method = request.method() == null || request.method().isEmpty()
                    ? "GET" : request.method().toUpperCase(java.util.Locale.ROOT);
            Dpop.Refusal r = Dpop.check(proof, token, jkt, method, request.url() == null ? "" : request.url(),
                    publicOrigin, now.getAsLong(), dpopReplay, dpopNonces);
            if (r != null) {
                return refuse.at(401, r.error(), r.description(), required, "DPoP");
            }
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
            return refuse.at(403, "insufficient_scope",
                    "The access token is missing the required scope(s): " + String.join(" ", missing) + ".", required, null);
        }
        Object clientId = claims.get("client_id") != null ? claims.get("client_id") : claims.get("azp");
        Object sub = claims.get("sub");
        return new Result(true, 200, null, "", "", Collections.unmodifiableMap(new LinkedHashMap<>(claims)), scopes,
                sub == null ? "" : String.valueOf(sub), clientId == null ? null : String.valueOf(clientId),
                jkt != null, nonce);
    }

    @FunctionalInterface
    private interface Refuser {
        Result at(int status, String error, String description, List<String> required, String scheme);
    }

    private Result refuse(int status, String error, String description, List<String> required, String scheme,
                          String nonce) {
        List<String> scope = (error == null || error.equals("insufficient_scope")) ? required : null;
        String challenge = scheme != null ? scheme : (requireDpop ? "DPoP" : "Bearer");
        return new Result(false, status, error, description,
                buildWwwAuthenticate(challenge, realm, error, description, scope, resourceMetadataUrl), Map.of(),
                List.of(), "", null, false, nonce);
    }

    /**
     * An RFC 6750 / RFC 9728 Bearer challenge. Without {@code error} it is the bare "authentication
     * required" challenge (no token was sent). Every argument is nullable.
     */
    public static String buildWwwAuthenticate(String realm, String error, String description, List<String> scope,
                                              String resourceMetadataUrl) {
        return buildWwwAuthenticate("Bearer", realm, error, description, scope, resourceMetadataUrl);
    }

    /**
     * An RFC 6750 / RFC 9449 / RFC 9728 challenge for {@code scheme} {@code Bearer} or {@code DPoP}
     * (a DPoP challenge also lists the accepted proof algorithms). Other arguments are nullable.
     */
    public static String buildWwwAuthenticate(String scheme, String realm, String error, String description,
                                              List<String> scope, String resourceMetadataUrl) {
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
        String sch = "DPoP".equals(scheme) ? "DPoP" : "Bearer";
        if (sch.equals("DPoP")) {
            parts.add("algs=" + quote(String.join(" ", Dpop.ALGS)));
        }
        return parts.isEmpty() ? sch : sch + " " + String.join(", ", parts);
    }

    /** The RFC 9728 protected-resource metadata document for your API. */
    public static Map<String, Object> protectedResourceMetadata(String resource, String issuer, List<String> scopesSupported) {
        return protectedResourceMetadata(resource, issuer, scopesSupported, false);
    }

    /**
     * The RFC 9728 protected-resource metadata document; {@code dpopBoundAccessTokensRequired}
     * advertises that tokens must be DPoP-bound (pair it with {@link Builder#requireDpop}).
     */
    public static Map<String, Object> protectedResourceMetadata(String resource, String issuer, List<String> scopesSupported,
                                                                boolean dpopBoundAccessTokensRequired) {
        Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("resource", resource);
        doc.put("authorization_servers", List.of(issuer.replaceAll("/+$", "")));
        doc.put("bearer_methods_supported", List.of("header"));
        if (scopesSupported != null && !scopesSupported.isEmpty()) {
            doc.put("scopes_supported", scopesSupported);
        }
        doc.put("dpop_signing_alg_values_supported", Dpop.ALGS);
        if (dpopBoundAccessTokensRequired) {
            doc.put("dpop_bound_access_tokens_required", true);
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
        private boolean requireDpop;
        private String publicOrigin;
        private DpopReplayStore dpopReplay;
        private DpopNonceSource dpopNonces;
        private Introspector introspector;

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
        /** Refuse every token that is not DPoP-bound and challenge with the {@code DPoP} scheme. */
        public Builder requireDpop(boolean v) { this.requireDpop = v; return this; }
        /** This API's public origin, for the DPoP {@code htu} when the request URL is a path (set it behind a proxy). */
        public Builder publicOrigin(String v) { this.publicOrigin = v; return this; }
        /** Where DPoP proof {@code jti}s are recorded; default a {@link MemoryDpopReplayStore} (one process). */
        public Builder dpopReplayStore(DpopReplayStore v) { this.dpopReplay = v; return this; }
        /** Require every DPoP proof to carry a nonce this source issued ({@code use_dpop_nonce} otherwise). */
        public Builder dpopNonces(DpopNonceSource v) { this.dpopNonces = v; return this; }
        /**
         * Validate opaque access tokens (and, with {@link Introspector.Builder#introspectJwts},
         * JWTs too) at Atlas's RFC 7662 introspection endpoint, cached briefly.
         */
        public Builder introspection(Introspector v) { this.introspector = v; return this; }

        public AccessTokenVerifier build() {
            return new AccessTokenVerifier(this);
        }
    }
}
