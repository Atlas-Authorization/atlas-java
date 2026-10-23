package net.atlasauth.atlas.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SsoConnection(
        String object,
        String id,
        String organizationId,
        String type,
        String status,
        String oidcIssuer,
        String oidcClientId,
        boolean hasSecret,
        String samlIdpEntityId,
        String samlIdpSsoUrl,
        String samlSpEntityId,
        boolean hasSamlCertificate,
        boolean samlAllowIdpInitiated,
        boolean samlSignAuthnRequests,
        boolean samlWantResponseSigned,
        boolean hasDiscourseSecret,
        String discourseProviderUrl,
        List<String> allowedDomains,
        List<SsoConnectionClaimRoleMapping> claimRoleMappings,
        String defaultRoleId,
        long createdAt,
        long updatedAt,
        /** SAML Single Logout is on for the connection. */ boolean samlSingleLogout,
        /** The IdP SingleLogoutService Location. */ String samlIdpSloUrl,
        /** The IdP SingleLogoutService ResponseLocation. */ String samlIdpSloResponseUrl,
        /** redirect or post. */ String samlIdpSloBinding,
        /** The IdP SOAP SingleLogoutService: the back channel for sessions ended server-side. */ String samlIdpSloSoapUrl,
        /** Assertions not encrypted to the connection's key are refused. */ boolean samlRequireEncryptedAssertions,
        /** An SP encryption key pair is set up (the private key is never returned). */ boolean hasSamlEncryptionKey,
        /** A key rotation is in progress (the previous key is still accepted). */ boolean hasPreviousSamlEncryptionKey,
        /** When the current encryption key was installed (epoch ms). */ Long samlEncryptionRotatedAt,
        /** OIDC: the declarative profile mapping. */ JsonNode profileMapping,
        /** OIDC: token-endpoint client authentication. */ String tokenEndpointAuthMethod,
        /** OIDC: the private_key_jwt assertion audience override. */ String clientAssertionAudience,
        /** OIDC: RP-initiated, back- and front-channel logout settings. */ JsonNode logout,
        /** OIDC: third-party initiated login settings. */ JsonNode idpInitiatedLogin,
        /** OIDC: the URLs to register at the IdP. */ UpstreamUrls upstreamUrls) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public SsoConnection(String object, String id, String organizationId, String type, String status, String oidcIssuer, String oidcClientId, boolean hasSecret, String samlIdpEntityId, String samlIdpSsoUrl, String samlSpEntityId, boolean hasSamlCertificate, boolean samlAllowIdpInitiated, boolean samlSignAuthnRequests, boolean samlWantResponseSigned, boolean hasDiscourseSecret, String discourseProviderUrl, List<String> allowedDomains, List<SsoConnectionClaimRoleMapping> claimRoleMappings, String defaultRoleId, long createdAt, long updatedAt) {
        this(object, id, organizationId, type, status, oidcIssuer, oidcClientId, hasSecret, samlIdpEntityId, samlIdpSsoUrl, samlSpEntityId, hasSamlCertificate, samlAllowIdpInitiated, samlSignAuthnRequests, samlWantResponseSigned, hasDiscourseSecret, discourseProviderUrl, allowedDomains, claimRoleMappings, defaultRoleId, createdAt, updatedAt, false, null, null, null, null, false, false, false, null, null, null, null, null, null, null);
    }
}
