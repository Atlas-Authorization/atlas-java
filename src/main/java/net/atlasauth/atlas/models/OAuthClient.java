package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A registered OAuth client (relying application) of this instance.
 *
 * @param skipLogoutConfirmation first-party clients only: when true, RP-initiated logout
 *                               ({@code /oauth2/logout}) with this client's {@code client_id} and an
 *                               exactly registered {@code post_logout_redirect_uri} ends the session and
 *                               redirects straight back (with {@code state}) without the confirmation page.
 *                               False by default.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClient(
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
        boolean skipLogoutConfirmation,
        /** After a rotation with overlap: when the previous secret stops working (epoch ms); null when only the current secret is accepted. */ Long previousSecretExpiresAt) {
    /** The 1.4 shape, without {@code previousSecretExpiresAt}. */
    public OAuthClient(String object, String id, String clientId, String name, String logoUrl, List<String> redirectUris, List<String> allowedScopes, List<String> grantTypes, String tokenEndpointAuthMethod, String secretPrefix, boolean isPublic, boolean firstParty, long createdAt, long updatedAt, boolean skipLogoutConfirmation) {
        this(object, id, clientId, name, logoUrl, redirectUris, allowedScopes, grantTypes, tokenEndpointAuthMethod, secretPrefix, isPublic, firstParty, createdAt, updatedAt, skipLogoutConfirmation, null);
    }

    /** The 1.2 shape, without {@code skipLogoutConfirmation}. */
    public OAuthClient(String object, String id, String clientId, String name, String logoUrl,
                       List<String> redirectUris, List<String> allowedScopes, List<String> grantTypes,
                       String tokenEndpointAuthMethod, String secretPrefix, boolean isPublic, boolean firstParty,
                       long createdAt, long updatedAt) {
        this(object, id, clientId, name, logoUrl, redirectUris, allowedScopes, grantTypes, tokenEndpointAuthMethod,
                secretPrefix, isPublic, firstParty, createdAt, updatedAt, false);
    }
}
