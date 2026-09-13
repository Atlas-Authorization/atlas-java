package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code POST /v1/sso_connections}. */
public final class CreateSsoConnectionBody {
    public String organizationId;
    /** One of {@code oidc}, {@code saml}, {@code discourse}. */
    public String type;
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

    public CreateSsoConnectionBody() {
    }

    public CreateSsoConnectionBody organizationId(String v) { this.organizationId = v; return this; }
    public CreateSsoConnectionBody type(String v) { this.type = v; return this; }
    public CreateSsoConnectionBody status(String v) { this.status = v; return this; }
    public CreateSsoConnectionBody oidcIssuer(String v) { this.oidcIssuer = v; return this; }
    public CreateSsoConnectionBody oidcClientId(String v) { this.oidcClientId = v; return this; }
    public CreateSsoConnectionBody oidcClientSecret(String v) { this.oidcClientSecret = v; return this; }
    public CreateSsoConnectionBody samlIdpEntityId(String v) { this.samlIdpEntityId = v; return this; }
    public CreateSsoConnectionBody samlIdpSsoUrl(String v) { this.samlIdpSsoUrl = v; return this; }
    public CreateSsoConnectionBody samlIdpCertificate(String v) { this.samlIdpCertificate = v; return this; }
    public CreateSsoConnectionBody samlSpEntityId(String v) { this.samlSpEntityId = v; return this; }
    public CreateSsoConnectionBody samlAllowIdpInitiated(Boolean v) { this.samlAllowIdpInitiated = v; return this; }
    public CreateSsoConnectionBody samlSignAuthnRequests(Boolean v) { this.samlSignAuthnRequests = v; return this; }
    public CreateSsoConnectionBody samlWantResponseSigned(Boolean v) { this.samlWantResponseSigned = v; return this; }
    public CreateSsoConnectionBody discourseSecret(String v) { this.discourseSecret = v; return this; }
    public CreateSsoConnectionBody discourseProviderUrl(String v) { this.discourseProviderUrl = v; return this; }
    public CreateSsoConnectionBody allowedDomains(List<String> v) { this.allowedDomains = v; return this; }
    public CreateSsoConnectionBody claimRoleMappings(List<SsoConnectionClaimRoleMapping> v) { this.claimRoleMappings = v; return this; }
    public CreateSsoConnectionBody defaultRoleId(String v) { this.defaultRoleId = v; return this; }
}
