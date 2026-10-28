package net.atlasauth.atlas.quarkus;

import jakarta.ws.rs.container.ContainerRequestContext;
import net.atlasauth.atlas.verify.AccessTokenVerifier;
import net.atlasauth.atlas.verify.SessionClaims;

/** Reads the verified session {@link AtlasAuthenticationFilter} stored on the request. */
public final class AtlasContext {

    /** The request property holding the verified {@link SessionClaims}. */
    public static final String PROPERTY = "atlas.claims";

    /** The request property holding the verified {@link AccessTokenVerifier.Result} of an OAuth access token. */
    public static final String ACCESS_TOKEN_PROPERTY = "atlas.accessToken";

    private AtlasContext() {
    }

    /** The verified claims, or {@code null} when the request carries no verified session. */
    public static SessionClaims claims(ContainerRequestContext request) {
        Object value = request == null ? null : request.getProperty(PROPERTY);
        return value instanceof SessionClaims claims ? claims : null;
    }

    /** The verified OAuth access token (subject, client, scopes, claims), or {@code null} when the request carries a session or nothing. */
    public static AccessTokenVerifier.Result accessToken(ContainerRequestContext request) {
        Object value = request == null ? null : request.getProperty(ACCESS_TOKEN_PROPERTY);
        return value instanceof AccessTokenVerifier.Result result ? result : null;
    }
}
