package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code POST /v1/oauth_clients}. */
public final class CreateOAuthClientBody {
    /** Lifetime of the issued secret (reported as client_secret_expires_at); null = never expires. */
    public Long secretExpiresInSeconds;
    /** "public" (default) or "pairwise" (a per-sector sub). */
    public String subjectType;
    /** An https URL listing every redirect_uri; required for a pairwise client spanning hosts. */
    public String sectorIdentifierUri;
    public String name;
    public List<String> redirectUris;
    public List<String> allowedScopes;
    public List<String> grantTypes;
    public String tokenEndpointAuthMethod;
    public String logoUrl;
    public Boolean firstParty;
    /**
     * First-party clients only (a third-party client is refused with 422
     * {@code VALIDATION_FAILED}, param {@code skip_logout_confirmation}). When true,
     * RP-initiated logout ({@code /oauth2/logout}) with this client's {@code client_id}
     * and an exactly registered {@code post_logout_redirect_uri} ends the session and
     * redirects straight back (with {@code state}) without the confirmation page.
     */
    public Boolean skipLogoutConfirmation;

    public CreateOAuthClientBody() {
    }

    public CreateOAuthClientBody(String name, List<String> redirectUris) {
        this.name = name;
        this.redirectUris = redirectUris;
    }

    public CreateOAuthClientBody name(String v) { this.name = v; return this; }
    public CreateOAuthClientBody redirectUris(List<String> v) { this.redirectUris = v; return this; }
    public CreateOAuthClientBody allowedScopes(List<String> v) { this.allowedScopes = v; return this; }
    public CreateOAuthClientBody grantTypes(List<String> v) { this.grantTypes = v; return this; }
    public CreateOAuthClientBody tokenEndpointAuthMethod(String v) { this.tokenEndpointAuthMethod = v; return this; }
    public CreateOAuthClientBody logoUrl(String v) { this.logoUrl = v; return this; }
    public CreateOAuthClientBody firstParty(Boolean v) { this.firstParty = v; return this; }
    public CreateOAuthClientBody skipLogoutConfirmation(Boolean v) { this.skipLogoutConfirmation = v; return this; }

    public CreateOAuthClientBody secretExpiresInSeconds(Long v) { this.secretExpiresInSeconds = v; return this; }
    public CreateOAuthClientBody subjectType(String v) { this.subjectType = v; return this; }
    public CreateOAuthClientBody sectorIdentifierUri(String v) { this.sectorIdentifierUri = v; return this; }
}
