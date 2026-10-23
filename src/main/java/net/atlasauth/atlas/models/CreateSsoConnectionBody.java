package net.atlasauth.atlas.models;

import java.util.Map;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
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

    public CreateSsoConnectionBody samlSingleLogout(Boolean v) { this.samlSingleLogout = v; return this; }
    public CreateSsoConnectionBody samlIdpSloUrl(String v) { this.samlIdpSloUrl = v; return this; }
    public CreateSsoConnectionBody samlIdpSloResponseUrl(String v) { this.samlIdpSloResponseUrl = v; return this; }
    public CreateSsoConnectionBody samlIdpSloBinding(String v) { this.samlIdpSloBinding = v; return this; }
    public CreateSsoConnectionBody samlIdpSloSoapUrl(String v) { this.samlIdpSloSoapUrl = v; return this; }
    public CreateSsoConnectionBody samlRequireEncryptedAssertions(Boolean v) { this.samlRequireEncryptedAssertions = v; return this; }
    public CreateSsoConnectionBody profileMapping(Map<String, Object> v) { this.profileMapping = v; return this; }
    public CreateSsoConnectionBody tokenEndpointAuthMethod(String v) { this.tokenEndpointAuthMethod = v; return this; }
    public CreateSsoConnectionBody clientAssertionAudience(String v) { this.clientAssertionAudience = v; return this; }
    public CreateSsoConnectionBody logout(Map<String, Object> v) { this.logout = v; return this; }
    public CreateSsoConnectionBody idpInitiatedLogin(Map<String, Object> v) { this.idpInitiatedLogin = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CreateSsoConnectionBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
