package net.atlasauth.atlas.rp;

import net.atlasauth.atlas.AtlasClient;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.LongSupplier;

/** Settings of a {@link RelyingParty}. Build with {@link #builder(String, String, String, String)}. */
public final class RpConfig {

    /** How the client authenticates at the token endpoint. */
    public enum ClientAuth { CLIENT_SECRET_BASIC, CLIENT_SECRET_POST, NONE }

    static final Set<String> NOT_FORWARDABLE = Set.of("response_type", "client_id", "redirect_uri", "state", "nonce", "code_challenge",
            "code_challenge_method", "scope", "return_to", "id_token_hint", "request", "request_uri");

    final String issuer;
    final String clientId;
    final String clientSecret;
    final ClientAuth clientAuth;
    final String redirectUri;
    final String secret;
    final String scope;
    final String afterSignInPath;
    final String postLogoutRedirectUri;
    final String secretKey;
    final String apiUrl;
    final String cookieName;
    final boolean serverSignOut;
    final long touchIntervalMs;
    final List<String> forwardParams;
    final HttpClient http;
    final LongSupplier now;

    private RpConfig(Builder b) {
        if (b.secret == null || b.secret.length() < 32) {
            throw new IllegalArgumentException("RpConfig: secret (at least 32 characters) is required to seal the sign-in cookie.");
        }
        if (!RelyingParty.isSafeReturnTo(b.afterSignInPath)) {
            throw new IllegalArgumentException("RpConfig: afterSignInPath must be a path starting with a single '/'.");
        }
        for (String name : b.forwardParams) {
            if (!name.matches("^[A-Za-z0-9_.-]{1,64}$") || NOT_FORWARDABLE.contains(name)) {
                throw new IllegalArgumentException("RpConfig: forwardParams cannot forward \"" + name + "\": use a plain parameter name the helper does not set itself.");
            }
        }
        this.issuer = b.issuer.replaceAll("/+$", "");
        this.clientId = b.clientId;
        this.clientSecret = b.clientSecret;
        this.clientAuth = b.clientAuth != null ? b.clientAuth : (b.clientSecret != null ? ClientAuth.CLIENT_SECRET_BASIC : ClientAuth.NONE);
        this.redirectUri = b.redirectUri;
        this.secret = b.secret;
        this.scope = b.scope;
        this.afterSignInPath = b.afterSignInPath;
        this.postLogoutRedirectUri = b.postLogoutRedirectUri;
        this.secretKey = b.secretKey;
        this.apiUrl = b.apiUrl.replaceAll("/+$", "");
        this.cookieName = b.cookieName;
        this.serverSignOut = b.serverSignOut;
        this.touchIntervalMs = b.touchIntervalMs;
        this.forwardParams = List.copyOf(b.forwardParams);
        this.http = b.http != null ? b.http : HttpClient.newHttpClient();
        this.now = b.now;
    }

    /**
     * @param issuer      your instance's issuer: {@code https://} plus its frontend API host
     * @param clientId    the OAuth client id
     * @param redirectUri the callback URL, registered on the client exactly
     * @param secret      at least 32 characters: seals the short-lived sign-in cookie
     */
    public static Builder builder(String issuer, String clientId, String redirectUri, String secret) {
        return new Builder(issuer, clientId, redirectUri, secret);
    }

    public static final class Builder {
        private final String issuer;
        private final String clientId;
        private final String redirectUri;
        private final String secret;
        private String clientSecret;
        private ClientAuth clientAuth;
        private String scope = "openid profile email";
        private String afterSignInPath = "/";
        private String postLogoutRedirectUri;
        private String secretKey;
        private String apiUrl = AtlasClient.DEFAULT_API_URL;
        private String cookieName = "atlas_oidc";
        private boolean serverSignOut;
        private long touchIntervalMs = 5 * 60_000;
        private final List<String> forwardParams = new ArrayList<>();
        private HttpClient http;
        private LongSupplier now = System::currentTimeMillis;

        private Builder(String issuer, String clientId, String redirectUri, String secret) {
            this.issuer = issuer;
            this.clientId = clientId;
            this.redirectUri = redirectUri;
            this.secret = secret;
        }

        /** The client secret (a confidential client). Without a {@code secretKey} it also authenticates {@code touchIfDue} and {@code endAppSession}. */
        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /** Token-endpoint authentication. Default {@code CLIENT_SECRET_BASIC} with a secret. */
        public Builder clientAuth(ClientAuth clientAuth) {
            this.clientAuth = clientAuth;
            return this;
        }

        /** Scopes. Default {@code openid profile email}. */
        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        /** Where the callback sends the browser when the sign-in carried no return path. Default {@code /}. */
        public Builder afterSignInPath(String path) {
            this.afterSignInPath = path;
            return this;
        }

        /** Where Atlas sends the browser after sign-out; registered on the client exactly. */
        public Builder postLogoutRedirectUri(String uri) {
            this.postLogoutRedirectUri = uri;
            return this;
        }

        /** The instance secret key (scope {@code sessions:write}) for {@code touchIfDue} and server-side revoke. */
        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder apiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        /** Flow cookie name. Default {@code atlas_oidc}. */
        public Builder cookieName(String cookieName) {
            this.cookieName = cookieName;
            return this;
        }

        /** Sign out with ONE server call ({@code POST /v1/oauth_clients/{id}/logout}); needs {@code oauth_clients:write} and {@code sessions:write}. */
        public Builder serverSignOut(boolean serverSignOut) {
            this.serverSignOut = serverSignOut;
            return this;
        }

        /** Minimum time between two keep-alive calls for one session. Default 5 minutes. */
        public Builder touchIntervalMs(long ms) {
            this.touchIntervalMs = ms;
            return this;
        }

        /** Query parameters to pass from your sign-in URL to {@code /oauth2/authorize} unchanged (for example {@code ref}); a value over 512 characters is dropped. */
        public Builder forwardParam(String name) {
            this.forwardParams.add(name);
            return this;
        }

        public Builder httpClient(HttpClient http) {
            this.http = http;
            return this;
        }

        /** A clock in epoch milliseconds (tests). */
        public Builder clock(LongSupplier now) {
            this.now = now;
            return this;
        }

        public RpConfig build() {
            return new RpConfig(this);
        }
    }
}
