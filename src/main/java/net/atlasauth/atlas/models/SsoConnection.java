package net.atlasauth.atlas.models;

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
        long updatedAt) {
}
