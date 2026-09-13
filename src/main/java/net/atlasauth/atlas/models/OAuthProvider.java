package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A single social sign-in provider row: the catalog entry plus whatever is
 * configured for this instance, and NEVER the secret — only {@code hasSecret}. The
 * {@code redirectUri} is derived from the instance's own FAPI host.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthProvider(
        String object,
        String provider,
        String displayName,
        String category,
        String tier,
        String setupDocsUrl,
        List<String> defaultScopes,
        List<OAuthProviderCredentialField> credentialFields,
        List<OAuthProviderSetting> settings,
        boolean native_,
        String redirectUri,
        boolean configured,
        String clientId,
        boolean hasSecret,
        boolean enabled,
        /** §6.5 which screens this provider serves. Both default true. */
        boolean allowSignIn,
        boolean allowSignUp,
        List<String> scopes,
        Long updatedAt) {

    // `native` is a Java keyword; the wire field is `native`, so map it explicitly.
    public OAuthProvider(
            String object,
            String provider,
            String displayName,
            String category,
            String tier,
            String setupDocsUrl,
            List<String> defaultScopes,
            List<OAuthProviderCredentialField> credentialFields,
            List<OAuthProviderSetting> settings,
            @com.fasterxml.jackson.annotation.JsonProperty("native") boolean native_,
            String redirectUri,
            boolean configured,
            String clientId,
            boolean hasSecret,
            boolean enabled,
            boolean allowSignIn,
            boolean allowSignUp,
            List<String> scopes,
            Long updatedAt) {
        this.object = object;
        this.provider = provider;
        this.displayName = displayName;
        this.category = category;
        this.tier = tier;
        this.setupDocsUrl = setupDocsUrl;
        this.defaultScopes = defaultScopes;
        this.credentialFields = credentialFields;
        this.settings = settings;
        this.native_ = native_;
        this.redirectUri = redirectUri;
        this.configured = configured;
        this.clientId = clientId;
        this.hasSecret = hasSecret;
        this.enabled = enabled;
        this.allowSignIn = allowSignIn;
        this.allowSignUp = allowSignUp;
        this.scopes = scopes;
        this.updatedAt = updatedAt;
    }
}
