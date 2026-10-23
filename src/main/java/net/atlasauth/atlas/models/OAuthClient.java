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
        /** After a rotation with overlap: when the previous secret stops working (epoch ms); null when only the current secret is accepted. */ Long previousSecretExpiresAt,
        /** Every OAuth flow for the client is refused while true. */ boolean disabled,
        /** PKCE is required for this confidential client too. */ boolean requirePkce,
        /** "public" (sub = the user id) or "pairwise" (a per-sector sub). */ String subjectType,
        /** The pairwise sector document, if registered. */ String sectorIdentifierUri,
        /** When the current secret stops authenticating (epoch ms); null = never. */ Long clientSecretExpiresAt,
        /** jwt or opaque access tokens for this client; null inherits from the API / instance. */ String accessTokenFormat,
        /** This client's token lifetime overrides (seconds); a null field is inherited. */ OAuthTokenLifetimes tokenLifetimes) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public OAuthClient(String object, String id, String clientId, String name, String logoUrl, List<String> redirectUris, List<String> allowedScopes, List<String> grantTypes, String tokenEndpointAuthMethod, String secretPrefix, boolean isPublic, boolean firstParty, long createdAt, long updatedAt, boolean skipLogoutConfirmation, Long previousSecretExpiresAt, boolean disabled, boolean requirePkce, String subjectType, String sectorIdentifierUri, Long clientSecretExpiresAt) {
        this(object, id, clientId, name, logoUrl, redirectUris, allowedScopes, grantTypes, tokenEndpointAuthMethod, secretPrefix, isPublic, firstParty, createdAt, updatedAt, skipLogoutConfirmation, previousSecretExpiresAt, disabled, requirePkce, subjectType, sectorIdentifierUri, clientSecretExpiresAt, null, null);
    }

    /** The 1.10 shape, before the 1.11 fields. */
    public OAuthClient(String object, String id, String clientId, String name, String logoUrl, List<String> redirectUris, List<String> allowedScopes, List<String> grantTypes, String tokenEndpointAuthMethod, String secretPrefix, boolean isPublic, boolean firstParty, long createdAt, long updatedAt, boolean skipLogoutConfirmation, Long previousSecretExpiresAt) {
        this(object, id, clientId, name, logoUrl, redirectUris, allowedScopes, grantTypes, tokenEndpointAuthMethod, secretPrefix, isPublic, firstParty, createdAt, updatedAt, skipLogoutConfirmation, previousSecretExpiresAt, false, false, null, null, null);
    }
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
