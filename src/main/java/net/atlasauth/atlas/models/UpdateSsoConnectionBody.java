package net.atlasauth.atlas.models;

import java.util.Map;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
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
    /** SAML Single Logout on/off. */
    public Boolean samlSingleLogout;
    /** The IdP SingleLogoutService URL (https). */
    public String samlIdpSloUrl;
    /** The IdP SingleLogoutService ResponseLocation (https). */
    public String samlIdpSloResponseUrl;
    /** redirect or post. */
    public String samlIdpSloBinding;
    /** The IdP SOAP SingleLogoutService (https): the back channel for sessions ended server-side. */
    public String samlIdpSloSoapUrl;
    /** Refuse assertions not encrypted to the connection's key (needs an encryption key first). */
    public Boolean samlRequireEncryptedAssertions;
    /** OIDC: declarative profile mapping ({@code fields}, {@code public_metadata}, {@code private_metadata}, {@code update_on_sign_in}). */
    public Map<String, Object> profileMapping;
    /** OIDC: client_secret_basic, client_secret_post, client_secret_jwt, private_key_jwt or none. */
    public String tokenEndpointAuthMethod;
    /** OIDC: the private_key_jwt assertion audience override. */
    public String clientAssertionAudience;
    /** OIDC: {@code rp_initiated}, {@code post_logout_redirect_url}, {@code backchannel}, {@code frontchannel}, {@code frontchannel_session_required}, {@code end_session_endpoint}. */
    public Map<String, Object> logout;
    /** OIDC: {@code enabled}, {@code allowed_targets}, {@code default_target}. */
    public Map<String, Object> idpInitiatedLogin;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

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

    public UpdateSsoConnectionBody samlSingleLogout(Boolean v) { this.samlSingleLogout = v; return this; }
    public UpdateSsoConnectionBody samlIdpSloUrl(String v) { this.samlIdpSloUrl = v; return this; }
    public UpdateSsoConnectionBody samlIdpSloResponseUrl(String v) { this.samlIdpSloResponseUrl = v; return this; }
    public UpdateSsoConnectionBody samlIdpSloBinding(String v) { this.samlIdpSloBinding = v; return this; }
    public UpdateSsoConnectionBody samlIdpSloSoapUrl(String v) { this.samlIdpSloSoapUrl = v; return this; }
    public UpdateSsoConnectionBody samlRequireEncryptedAssertions(Boolean v) { this.samlRequireEncryptedAssertions = v; return this; }
    public UpdateSsoConnectionBody profileMapping(Map<String, Object> v) { this.profileMapping = v; return this; }
    public UpdateSsoConnectionBody tokenEndpointAuthMethod(String v) { this.tokenEndpointAuthMethod = v; return this; }
    public UpdateSsoConnectionBody clientAssertionAudience(String v) { this.clientAssertionAudience = v; return this; }
    public UpdateSsoConnectionBody logout(Map<String, Object> v) { this.logout = v; return this; }
    public UpdateSsoConnectionBody idpInitiatedLogin(Map<String, Object> v) { this.idpInitiatedLogin = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public UpdateSsoConnectionBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
