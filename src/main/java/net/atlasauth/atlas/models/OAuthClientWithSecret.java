package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

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
        boolean skipLogoutConfirmation) {

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
