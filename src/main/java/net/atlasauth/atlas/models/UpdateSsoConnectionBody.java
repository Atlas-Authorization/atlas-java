package net.atlasauth.atlas.models;

import java.util.List;

/**
 * Body for {@code PATCH /v1/sso_connections/:id}. Accepts everything create does
 * except {@code type}, which is immutable.
 */
public final class UpdateSsoConnectionBody {
    public String organizationId;
    /** One of {@code draft}, {@code active}, {@code disabled}. */
    public String status;
    public String oidcIssuer;
    public String oidcClientId;
    /** Write-only; stored encrypted, never returned. */
    public String oidcClientSecret;
    public String samlIdpEntityId;
    public String samlIdpSsoUrl;
    /** Write-only; only {@code has_saml_certificate} is ever returned. */
    public String samlIdpCertificate;
    public String samlSpEntityId;
    public Boolean samlAllowIdpInitiated;
    public Boolean samlSignAuthnRequests;
    public Boolean samlWantResponseSigned;
    public String discourseSecret;
    public String discourseProviderUrl;
    public List<String> allowedDomains;
    public List<SsoConnectionClaimRoleMapping> claimRoleMappings;
    public String defaultRoleId;

    public UpdateSsoConnectionBody() {
    }

    public UpdateSsoConnectionBody organizationId(String v) { this.organizationId = v; return this; }
    public UpdateSsoConnectionBody status(String v) { this.status = v; return this; }
    public UpdateSsoConnectionBody oidcIssuer(String v) { this.oidcIssuer = v; return this; }
    public UpdateSsoConnectionBody oidcClientId(String v) { this.oidcClientId = v; return this; }
    public UpdateSsoConnectionBody oidcClientSecret(String v) { this.oidcClientSecret = v; return this; }
    public UpdateSsoConnectionBody samlIdpEntityId(String v) { this.samlIdpEntityId = v; return this; }
    public UpdateSsoConnectionBody samlIdpSsoUrl(String v) { this.samlIdpSsoUrl = v; return this; }
    public UpdateSsoConnectionBody samlIdpCertificate(String v) { this.samlIdpCertificate = v; return this; }
    public UpdateSsoConnectionBody samlSpEntityId(String v) { this.samlSpEntityId = v; return this; }
    public UpdateSsoConnectionBody samlAllowIdpInitiated(Boolean v) { this.samlAllowIdpInitiated = v; return this; }
    public UpdateSsoConnectionBody samlSignAuthnRequests(Boolean v) { this.samlSignAuthnRequests = v; return this; }
    public UpdateSsoConnectionBody samlWantResponseSigned(Boolean v) { this.samlWantResponseSigned = v; return this; }
    public UpdateSsoConnectionBody discourseSecret(String v) { this.discourseSecret = v; return this; }
    public UpdateSsoConnectionBody discourseProviderUrl(String v) { this.discourseProviderUrl = v; return this; }
    public UpdateSsoConnectionBody allowedDomains(List<String> v) { this.allowedDomains = v; return this; }
    public UpdateSsoConnectionBody claimRoleMappings(List<SsoConnectionClaimRoleMapping> v) { this.claimRoleMappings = v; return this; }
    public UpdateSsoConnectionBody defaultRoleId(String v) { this.defaultRoleId = v; return this; }
}
