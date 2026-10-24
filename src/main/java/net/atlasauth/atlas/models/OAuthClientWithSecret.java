package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Create/rotate reveal the client secret exactly once, alongside the full client.
 * (Java records can't extend, so this mirrors every {@link OAuthClient} field plus
 * {@code clientSecret} and {@code note}.)
 *
 * @param skipLogoutConfirmation see {@link OAuthClient#skipLogoutConfirmation()}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientWithSecret(
        String object,
        String id,
        String clientId,
        String name,
        String logoUrl,
        List<String> redirectUris,
        List<String> allowedScopes,
        List<String> grantTypes,
        String tokenEndpointAuthMethod,
        String secretPrefix,
        boolean isPublic,
        boolean firstParty,
        long createdAt,
        long updatedAt,
        /** Revealed exactly once at create/rotate. */
        String clientSecret,
        String note,
        boolean skipLogoutConfirmation,
        /** On a rotation: when the previous secret stops working (epoch ms), or null when it already has. */ Long previousSecretExpiresAt,
        /** On a rotation: how long the previous secret keeps working, in seconds (echoed). */ Integer overlapSeconds,
        /** Provisioning tags (strings, numbers and booleans). */ Map<String, Object> metadata,
        /** Your idempotent-create key. */ String externalRef,
        /** True on a create that sent an {@code external_ref} and got the existing client back (200, no secret). */ Boolean idempotentReplay,
        /** With an `X-Secret-Recipient`: the secret sealed to it (then the plaintext field is absent). */ String sealedSecret,
        /** With an `X-Secret-Recipient`: the client secret sealed to it. */ String sealedClientSecret,
        /** With an `X-Secret-Recipient`: the webhook secret sealed to it. */ String sealedWebhookSecret,
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed) {
    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public OAuthClientWithSecret(String object, String id, String clientId, String name, String logoUrl, List<String> redirectUris, List<String> allowedScopes, List<String> grantTypes, String tokenEndpointAuthMethod, String secretPrefix, boolean isPublic, boolean firstParty, long createdAt, long updatedAt, String clientSecret, String note, boolean skipLogoutConfirmation, Long previousSecretExpiresAt, Integer overlapSeconds, Map<String, Object> metadata, String externalRef, Boolean idempotentReplay) {
        this(object, id, clientId, name, logoUrl, redirectUris, allowedScopes, grantTypes, tokenEndpointAuthMethod, secretPrefix, isPublic, firstParty, createdAt, updatedAt, clientSecret, note, skipLogoutConfirmation, previousSecretExpiresAt, overlapSeconds, metadata, externalRef, idempotentReplay, null, null, null, null);
    }

    /** The shape before the provisioning fields. */
    public OAuthClientWithSecret(String object, String id, String clientId, String name, String logoUrl, List<String> redirectUris, List<String> allowedScopes, List<String> grantTypes, String tokenEndpointAuthMethod, String secretPrefix, boolean isPublic, boolean firstParty, long createdAt, long updatedAt, String clientSecret, String note, boolean skipLogoutConfirmation, Long previousSecretExpiresAt, Integer overlapSeconds) {
        this(object, id, clientId, name, logoUrl, redirectUris, allowedScopes, grantTypes, tokenEndpointAuthMethod, secretPrefix, isPublic, firstParty, createdAt, updatedAt, clientSecret, note, skipLogoutConfirmation, previousSecretExpiresAt, overlapSeconds, null, null, null);
    }

    /** The 1.4 shape, without {@code previousSecretExpiresAt}, {@code overlapSeconds}. */
    public OAuthClientWithSecret(String object, String id, String clientId, String name, String logoUrl, List<String> redirectUris, List<String> allowedScopes, List<String> grantTypes, String tokenEndpointAuthMethod, String secretPrefix, boolean isPublic, boolean firstParty, long createdAt, long updatedAt, String clientSecret, String note, boolean skipLogoutConfirmation) {
        this(object, id, clientId, name, logoUrl, redirectUris, allowedScopes, grantTypes, tokenEndpointAuthMethod, secretPrefix, isPublic, firstParty, createdAt, updatedAt, clientSecret, note, skipLogoutConfirmation, null, null);
    }

    /** The 1.2 shape, without {@code skipLogoutConfirmation}. */
    public OAuthClientWithSecret(String object, String id, String clientId, String name, String logoUrl,
                                 List<String> redirectUris, List<String> allowedScopes, List<String> grantTypes,
                                 String tokenEndpointAuthMethod, String secretPrefix, boolean isPublic,
                                 boolean firstParty, long createdAt, long updatedAt, String clientSecret,
                                 String note) {
        this(object, id, clientId, name, logoUrl, redirectUris, allowedScopes, grantTypes, tokenEndpointAuthMethod,
                secretPrefix, isPublic, firstParty, createdAt, updatedAt, clientSecret, note, false);
    }
}
