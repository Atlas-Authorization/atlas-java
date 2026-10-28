package net.atlasauth.atlas.micronaut;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.validator.TokenValidator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.atlasauth.atlas.verify.AccessTokenVerifier;
import net.atlasauth.atlas.verify.SessionClaims;
import net.atlasauth.atlas.verify.SessionVerifier;
import net.atlasauth.atlas.verify.VerifyResult;
import org.reactivestreams.Publisher;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validates an Atlas session token locally (cached JWKS, issuer, expiry with 5 seconds of skew, optional
 * {@code azp} allowlist) and turns it into a Micronaut Security {@link Authentication}, so
 * {@code @Secured(SecurityRule.IS_AUTHENTICATED)} and {@code @Secured("admin")} work.
 *
 * <p>The name is the Atlas user id. Roles are the organization role, app roles, and organization and
 * app permissions. The attributes carry {@code sid}, {@code org_id}, {@code org_role} and the full
 * {@link SessionClaims} under {@value #CLAIMS}. Anything that does not verify yields no authentication
 * (a 401): the validator fails closed.
 */
@Singleton
@Requires(property = "atlas.issuer")
public class AtlasTokenValidator implements TokenValidator<HttpRequest<?>> {

    /** The attribute holding the verified {@link SessionClaims}. */
    public static final String CLAIMS = "atlas.claims";

    private final SessionVerifier verifier;

    /** The attribute holding the verified {@link AccessTokenVerifier.Result} of an OAuth access token. */
    public static final String ACCESS_TOKEN = "atlas.accessToken";

    private final AccessTokenVerifier accessVerifier;

    public AtlasTokenValidator(SessionVerifier verifier) {
        this(verifier, null);
    }

    @Inject
    public AtlasTokenValidator(SessionVerifier verifier, @Nullable AccessTokenVerifier accessVerifier) {
        this.verifier = verifier;
        this.accessVerifier = accessVerifier;
    }

    @Override
    public Publisher<Authentication> validateToken(String token, HttpRequest<?> request) {
        try {
            VerifyResult result = verifier.verify(token);
            if (!result.ok()) {
                return accessToken(token, request);
            }
            SessionClaims claims = result.claims();
            Set<String> roles = new LinkedHashSet<>();
            if (claims.orgRole() != null && !claims.orgRole().isEmpty()) {
                roles.add(claims.orgRole());
            }
            roles.addAll(claims.appRoles());
            roles.addAll(claims.orgPermissions());
            roles.addAll(claims.appPermissions());
            Map<String, Object> attributes = new LinkedHashMap<>();
            attributes.put("sid", claims.sid());
            if (claims.orgId() != null) {
                attributes.put("org_id", claims.orgId());
            }
            if (claims.orgRole() != null) {
                attributes.put("org_role", claims.orgRole());
            }
            attributes.put(CLAIMS, claims);
            return Publishers.just(Authentication.build(claims.sub(), roles, attributes));
        } catch (RuntimeException e) {
            return Publishers.empty();
        }
    }

    /**
     * Not a session: an OAuth access token for this API. Roles are its scopes, so {@code @Secured("orders:read")}
     * works; the name is the token's subject. A token that does not say {@code token_use: access_token} is refused.
     * Only the Authorization header is trusted for access tokens, never the session cookie.
     */
    private Publisher<Authentication> accessToken(String token, HttpRequest<?> request) {
        if (accessVerifier == null) {
            return Publishers.empty();
        }
        String authorization = request == null ? null : request.getHeaders().get("Authorization");
        if (authorization == null || !authorization.trim().regionMatches(true, 0, "Bearer ", 0, 7)
                || !authorization.trim().substring(7).trim().equals(token)) {
            return Publishers.empty();
        }
        AccessTokenVerifier.Result at = accessVerifier.verify("Bearer " + token);
        if (!at.ok() || !"access_token".equals(at.claims().get("token_use"))) {
            return Publishers.empty();
        }
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("scopes", at.scopes());
        if (at.clientId() != null) {
            attributes.put("client_id", at.clientId());
        }
        attributes.put(ACCESS_TOKEN, at);
        return Publishers.just(Authentication.build(at.subject(), new LinkedHashSet<>(at.scopes()), attributes));
    }
}
