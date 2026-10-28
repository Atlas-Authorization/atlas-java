package net.atlasauth.atlas.quarkus;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import net.atlasauth.atlas.verify.AccessTokenVerifier;
import net.atlasauth.atlas.verify.ProtectCondition;
import net.atlasauth.atlas.verify.SessionClaims;
import net.atlasauth.atlas.verify.SessionVerifier;
import net.atlasauth.atlas.verify.VerifyResult;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Verifies the Atlas session (or, when configured, the OAuth access token) of every request to a resource marked {@link AtlasAuthenticated}:
 * {@code Authorization: Bearer} first, then the {@code __session} cookie, checked locally against
 * the cached key set. Fails closed: no token, a bad token or any verifier error is a {@code 401};
 * a valid session that does not meet {@link AtlasRequires} is a {@code 403}.
 *
 * <p>On success the claims are on the request ({@link AtlasContext#claims}) and the JAX-RS
 * {@link SecurityContext} is replaced: the principal name is the user id and
 * {@code isUserInRole} answers for the organization role, app roles and permissions.
 */
@Provider
@AtlasAuthenticated
@Priority(Priorities.AUTHENTICATION)
public class AtlasAuthenticationFilter implements ContainerRequestFilter {

    static final String COOKIE = "__session";
    private static final Pattern BEARER = Pattern.compile("^Bearer\\s+(\\S+)$", Pattern.CASE_INSENSITIVE);

    @Inject
    SessionVerifier verifier;

    @Inject
    AtlasAccessTokens accessTokens;

    @Context
    ResourceInfo resourceInfo;

    public AtlasAuthenticationFilter() {
    }

    AtlasAuthenticationFilter(SessionVerifier verifier, ResourceInfo resourceInfo) {
        this(verifier, null, resourceInfo);
    }

    AtlasAuthenticationFilter(SessionVerifier verifier, AtlasAccessTokens accessTokens, ResourceInfo resourceInfo) {
        this.verifier = verifier;
        this.accessTokens = accessTokens;
        this.resourceInfo = resourceInfo;
    }

    @Override
    public void filter(ContainerRequestContext request) {
        SessionClaims claims;
        try {
            String bearer = bearer(request);
            String token = bearer != null ? bearer : cookie(request);
            VerifyResult result = token == null ? null : verifier.verify(token);
            if (result == null || !result.ok()) {
                // Not a session: an OAuth access token for this API, from the Authorization header only.
                AccessTokenVerifier access = accessTokens == null ? null : accessTokens.verifier();
                if (bearer != null && access != null) {
                    AccessTokenVerifier.Result at = access.verify("Bearer " + bearer);
                    if (at.ok() && "access_token".equals(at.claims().get("token_use"))) {
                        acceptAccessToken(request, at);
                        return;
                    }
                    if (at.status() == 403) {
                        request.abortWith(json(Response.Status.FORBIDDEN, "insufficient_scope").build());
                        return;
                    }
                }
                unauthorized(request);
                return;
            }
            claims = result.claims();
        } catch (RuntimeException e) {
            unauthorized(request);
            return;
        }
        AtlasRequires requires = requires();
        if (requires != null && (requires.scopes().length > 0 || !SessionVerifier.has(claims, condition(requires)))) {
            request.abortWith(json(Response.Status.FORBIDDEN, "forbidden").build());
            return;
        }
        request.setProperty(AtlasContext.PROPERTY, claims);
        request.setSecurityContext(new AtlasSecurityContext(claims, request.getSecurityContext()));
    }

    private void acceptAccessToken(ContainerRequestContext request, AccessTokenVerifier.Result at) {
        AtlasRequires requires = requires();
        // An access token carries no organization claims: only scope requirements can be met.
        if (requires != null && (!at.scopes().containsAll(List.of(requires.scopes())) || !condition(requires).isEmpty())) {
            request.abortWith(json(Response.Status.FORBIDDEN, "forbidden").build());
            return;
        }
        request.setProperty(AtlasContext.ACCESS_TOKEN_PROPERTY, at);
        request.setSecurityContext(new AccessTokenSecurityContext(at, request.getSecurityContext()));
    }

    private static String bearer(ContainerRequestContext request) {
        String authorization = request.getHeaderString("Authorization");
        if (authorization != null) {
            Matcher m = BEARER.matcher(authorization.trim());
            if (m.matches()) {
                return m.group(1);
            }
        }
        return null;
    }

    private static String cookie(ContainerRequestContext request) {
        Cookie cookie = request.getCookies() == null ? null : request.getCookies().get(COOKIE);
        return cookie != null && cookie.getValue() != null && !cookie.getValue().isEmpty() ? cookie.getValue() : null;
    }

    private AtlasRequires requires() {
        if (resourceInfo == null) {
            return null;
        }
        Method method = resourceInfo.getResourceMethod();
        AtlasRequires onMethod = method == null ? null : method.getAnnotation(AtlasRequires.class);
        if (onMethod != null) {
            return onMethod;
        }
        Class<?> type = resourceInfo.getResourceClass();
        return type == null ? null : type.getAnnotation(AtlasRequires.class);
    }

    static ProtectCondition condition(AtlasRequires r) {
        ProtectCondition c = new ProtectCondition();
        if (!r.role().isEmpty()) {
            c.role = r.role();
        }
        if (!r.permission().isEmpty()) {
            c.permission = r.permission();
        }
        if (r.anyPermission().length > 0) {
            c.anyPermission = List.of(r.anyPermission());
        }
        if (r.allPermissions().length > 0) {
            c.allPermissions = List.of(r.allPermissions());
        }
        if (!r.appRole().isEmpty()) {
            c.appRole = r.appRole();
        }
        if (!r.appPermission().isEmpty()) {
            c.appPermission = r.appPermission();
        }
        return c;
    }

    private static void unauthorized(ContainerRequestContext request) {
        request.abortWith(json(Response.Status.UNAUTHORIZED, "unauthorized")
                .header("WWW-Authenticate", "Bearer realm=\"atlas\"").build());
    }

    private static Response.ResponseBuilder json(Response.Status status, String error) {
        return Response.status(status).type(MediaType.APPLICATION_JSON).entity("{\"error\":\"" + error + "\"}");
    }

    private static final class AtlasSecurityContext implements SecurityContext {
        private final SessionClaims claims;
        private final SecurityContext delegate;

        AtlasSecurityContext(SessionClaims claims, SecurityContext delegate) {
            this.claims = claims;
            this.delegate = delegate;
        }

        @Override
        public Principal getUserPrincipal() {
            String sub = claims.sub();
            return () -> sub;
        }

        @Override
        public boolean isUserInRole(String role) {
            List<String> roles = new ArrayList<>(claims.appRoles());
            roles.addAll(claims.orgPermissions());
            roles.addAll(claims.appPermissions());
            if (claims.orgRole() != null) {
                roles.add(claims.orgRole());
            }
            return role != null && roles.stream().anyMatch(role::equals);
        }

        @Override
        public boolean isSecure() {
            return delegate != null && delegate.isSecure();
        }

        @Override
        public String getAuthenticationScheme() {
            return "Bearer";
        }
    }

    private static final class AccessTokenSecurityContext implements SecurityContext {
        private final AccessTokenVerifier.Result token;
        private final SecurityContext delegate;

        AccessTokenSecurityContext(AccessTokenVerifier.Result token, SecurityContext delegate) {
            this.token = token;
            this.delegate = delegate;
        }

        @Override
        public Principal getUserPrincipal() {
            String sub = token.subject();
            return () -> sub;
        }

        @Override
        public boolean isUserInRole(String role) {
            return role != null && token.scopes().contains(role);
        }

        @Override
        public boolean isSecure() {
            return delegate != null && delegate.isSecure();
        }

        @Override
        public String getAuthenticationScheme() {
            return "Bearer";
        }
    }
}
