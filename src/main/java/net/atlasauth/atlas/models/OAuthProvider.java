package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * A single social sign-in provider row: the catalog entry plus whatever is
 * configured for this instance, and NEVER the secret — only {@code hasSecret}. The
 * {@code redirectUri} is derived from the instance's own FAPI host.
 *
 * <p>{@code settingValues} holds the stored non-secret settings (for the wallet
 * providers: the verifier settings such as {@code domain} and {@code chain_ids});
 * {@code mode} is {@code "native"} or {@code "redirect"}; {@code iconUrl} and
 * {@code displayName} reflect any button re-branding.
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
        // `native` is a Java keyword; the wire field is `native`, so map it explicitly.
        @JsonProperty("native") boolean native_,
        String redirectUri,
        boolean configured,
        String clientId,
        boolean hasSecret,
        boolean enabled,
        /** §6.5 which screens this provider serves. Both default true. */
        boolean allowSignIn,
        boolean allowSignUp,
        List<String> scopes,
        Long updatedAt,
        List<String> apiScopes,
        Map<String, String> settingValues,
        String mode,
        List<String> javascriptOrigins,
        String atlasCallback,
        String customRedirectUri,
        String iconUrl) {
}
