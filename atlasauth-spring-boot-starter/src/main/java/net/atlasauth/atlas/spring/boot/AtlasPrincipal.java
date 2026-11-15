package net.atlasauth.atlas.spring.boot;

import net.atlasauth.atlas.verify.SessionClaims;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * The signed-in user as Spring Security sees it: the verified claims of an Atlas session token.
 * Inject it with {@code @AuthenticationPrincipal AtlasPrincipal user}.
 */
public final class AtlasPrincipal implements Principal {

    private final SessionClaims claims;

    public AtlasPrincipal(SessionClaims claims) {
        this.claims = claims;
    }

    /** The user id ({@code sub}). */
    public String id() {
        return claims.sub();
    }

    @Override
    public String getName() {
        return claims.sub();
    }

    /** The Atlas session id ({@code sid}). */
    public String sessionId() {
        return claims.sid();
    }

    /** The active organization id, or null. */
    public String orgId() {
        return claims.orgId();
    }

    public String orgSlug() {
        return claims.orgSlug();
    }

    /** The user's role in the active organization, or null. */
    public String orgRole() {
        return claims.orgRole();
    }

    public List<String> orgPermissions() {
        return claims.orgPermissions();
    }

    /** The user's instance-wide app roles. */
    public List<String> appRoles() {
        return claims.appRoles();
    }

    public List<String> appPermissions() {
        return claims.appPermissions();
    }

    public boolean mfa() {
        return claims.usedMultiFactor() || claims.mfa();
    }

    /** Whether the user signed in (or stepped up) within {@code maxAgeSeconds}: the gate for a sensitive action. */
    public boolean authenticatedWithin(long maxAgeSeconds) {
        return claims.authenticatedWithin(maxAgeSeconds);
    }

    /** One claim by name (null when absent). */
    public Object claim(String name) {
        return claims.get(name);
    }

    /** Every claim, as it appeared in the token. */
    public Map<String, Object> claims() {
        return claims.raw();
    }

    /** The typed claims. */
    public SessionClaims sessionClaims() {
        return claims;
    }
}
