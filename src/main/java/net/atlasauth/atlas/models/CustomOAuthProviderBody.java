package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Body for creating ({@code providerKey} and {@code displayName} required) or patching a
 * custom OAuth / OIDC provider. {@code clientSecret} is write-only. Use
 * {@link #set(String, Object)} to send an explicit {@code null} that clears a setting.
 */
public final class CustomOAuthProviderBody {
    public String providerKey;
    public String displayName;
    public String iconUrl;
    /** oidc (default) or oauth2. */
    public String protocol;
    public String issuer;
    public String discoveryUrl;
    public String authorizationEndpoint;
    public String tokenEndpoint;
    public String userinfoEndpoint;
    public String jwksUri;
    public String endSessionEndpoint;
    public List<String> scopes;
    /** " ", "," or "+". */
    public String scopeSeparator;
    public Boolean pkce;
    public Map<String, String> authorizationParams;
    public Map<String, String> tokenParams;
    /** header or query. */
    public String userinfoAuth;
    /** client_secret_basic, client_secret_post, client_secret_jwt, private_key_jwt or none. */
    public String tokenEndpointAuthMethod;
    public String clientAssertionAudience;
    /** merged, userinfo or id_token. */
    public String profileSource;
    /** {@code fields}, {@code public_metadata}, {@code private_metadata}, {@code update_on_sign_in}. */
    public Map<String, Object> profileMapping;
    public Map<String, Object> logout;
    public Map<String, Object> idpInitiatedLogin;
    public String clientId;
    /** Write-only; never returned. */
    public String clientSecret;
    public Boolean enabled;
    public Boolean allowSignIn;
    public Boolean allowSignUp;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CustomOAuthProviderBody() {
    }

    public CustomOAuthProviderBody providerKey(String v) { this.providerKey = v; return this; }
    public CustomOAuthProviderBody displayName(String v) { this.displayName = v; return this; }
    public CustomOAuthProviderBody iconUrl(String v) { this.iconUrl = v; return this; }
    public CustomOAuthProviderBody protocol(String v) { this.protocol = v; return this; }
    public CustomOAuthProviderBody issuer(String v) { this.issuer = v; return this; }
    public CustomOAuthProviderBody discoveryUrl(String v) { this.discoveryUrl = v; return this; }
    public CustomOAuthProviderBody authorizationEndpoint(String v) { this.authorizationEndpoint = v; return this; }
    public CustomOAuthProviderBody tokenEndpoint(String v) { this.tokenEndpoint = v; return this; }
    public CustomOAuthProviderBody userinfoEndpoint(String v) { this.userinfoEndpoint = v; return this; }
    public CustomOAuthProviderBody jwksUri(String v) { this.jwksUri = v; return this; }
    public CustomOAuthProviderBody endSessionEndpoint(String v) { this.endSessionEndpoint = v; return this; }
    public CustomOAuthProviderBody scopes(List<String> v) { this.scopes = v; return this; }
    public CustomOAuthProviderBody scopeSeparator(String v) { this.scopeSeparator = v; return this; }
    public CustomOAuthProviderBody pkce(Boolean v) { this.pkce = v; return this; }
    public CustomOAuthProviderBody authorizationParams(Map<String, String> v) { this.authorizationParams = v; return this; }
    public CustomOAuthProviderBody tokenParams(Map<String, String> v) { this.tokenParams = v; return this; }
    public CustomOAuthProviderBody userinfoAuth(String v) { this.userinfoAuth = v; return this; }
    public CustomOAuthProviderBody tokenEndpointAuthMethod(String v) { this.tokenEndpointAuthMethod = v; return this; }
    public CustomOAuthProviderBody clientAssertionAudience(String v) { this.clientAssertionAudience = v; return this; }
    public CustomOAuthProviderBody profileSource(String v) { this.profileSource = v; return this; }
    public CustomOAuthProviderBody profileMapping(Map<String, Object> v) { this.profileMapping = v; return this; }
    public CustomOAuthProviderBody logout(Map<String, Object> v) { this.logout = v; return this; }
    public CustomOAuthProviderBody idpInitiatedLogin(Map<String, Object> v) { this.idpInitiatedLogin = v; return this; }
    public CustomOAuthProviderBody clientId(String v) { this.clientId = v; return this; }
    public CustomOAuthProviderBody clientSecret(String v) { this.clientSecret = v; return this; }
    public CustomOAuthProviderBody enabled(Boolean v) { this.enabled = v; return this; }
    public CustomOAuthProviderBody allowSignIn(Boolean v) { this.allowSignIn = v; return this; }
    public CustomOAuthProviderBody allowSignUp(Boolean v) { this.allowSignUp = v; return this; }

    /** Set any field by its wire name, including an explicit {@code null} (clears it). */
    public CustomOAuthProviderBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
