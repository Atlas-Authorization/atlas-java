package net.atlasauth.atlas.spring.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * {@code atlas.login.*}: the hosted-redirect sign-in (OpenID Connect relying party) for server-rendered apps. Off
 * until {@code atlas.login.client-id} is set. The issuer is {@code atlas.issuer}, and {@code atlas.secret-key}
 * (when present) keeps the Atlas session alive and revokes it on sign-out.
 *
 * <pre>
 * atlas:
 *   issuer: https://clerk.example.com
 *   login:
 *     client-id: oc_...
 *     client-secret: ${ATLAS_CLIENT_SECRET}
 *     redirect-uri: https://app.example.com/auth/callback
 *     secret: ${ATLAS_LOGIN_SECRET}    # 32+ characters; seals the sign-in and session cookies
 *     protected-paths: [/app/**]
 * </pre>
 */
@ConfigurationProperties("atlas.login")
public class AtlasLoginProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    /** At least 32 characters: seals the sign-in and session cookies. */
    private String secret;
    private String scope = "openid profile email";
    private String signInPath = "/auth/sign-in";
    private String signOutPath = "/auth/sign-out";
    private String afterSignInPath = "/";
    private String postLogoutRedirectUri;
    private boolean serverSignOut;
    /** Path prefixes an anonymous request may not reach: it is sent to sign-in and back. */
    private List<String> protectedPaths = List.of();

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSignInPath() {
        return signInPath;
    }

    public void setSignInPath(String signInPath) {
        this.signInPath = signInPath;
    }

    public String getSignOutPath() {
        return signOutPath;
    }

    public void setSignOutPath(String signOutPath) {
        this.signOutPath = signOutPath;
    }

    public String getAfterSignInPath() {
        return afterSignInPath;
    }

    public void setAfterSignInPath(String afterSignInPath) {
        this.afterSignInPath = afterSignInPath;
    }

    public String getPostLogoutRedirectUri() {
        return postLogoutRedirectUri;
    }

    public void setPostLogoutRedirectUri(String postLogoutRedirectUri) {
        this.postLogoutRedirectUri = postLogoutRedirectUri;
    }

    public boolean isServerSignOut() {
        return serverSignOut;
    }

    public void setServerSignOut(boolean serverSignOut) {
        this.serverSignOut = serverSignOut;
    }

    public List<String> getProtectedPaths() {
        return protectedPaths;
    }

    public void setProtectedPaths(List<String> protectedPaths) {
        this.protectedPaths = protectedPaths;
    }
}
