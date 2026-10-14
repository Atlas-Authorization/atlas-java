package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateAtlasIdAppBody {
    public String name;
    public List<String> redirectUris;
    /** Where /oauth2/logout may send the user after sign-out. Exact match; https, or http on a loopback host (localhost, 127.0.0.1, [::1]). Defaults to []. */
    public List<String> postLogoutRedirectUris;
    /** OIDC Back-Channel Logout: when the user signs out of Atlas, a signed logout_token is POSTed here (form field `logout_token`). Same URL rule as redirect_uris; empty or null clears it. */
    public String backchannelLogoutUri;
    /** Whether the app needs `sid` in the logout_token. Atlas always includes it. Default false. */
    public Boolean backchannelLogoutSessionRequired;
    /** OIDC Front-Channel Logout: loaded in a hidden iframe on the Atlas sign-out page, with `iss` (and `sid` if session-required). Same URL rule as redirect_uris; empty or null clears it. */
    public String frontchannelLogoutUri;
    /** Add `sid` to the front-channel logout URL. Default false. */
    public Boolean frontchannelLogoutSessionRequired;
    public String logoUrl;
    public String homepageUrl;
    public List<String> allowedScopes;
    /** How the Sign in with Atlas page and consent screen look for this app. On PATCH each field merges with what is stored; a field set to null clears it, and branding: null clears all of them. The Atlas header and the app domain are always shown. */
    public AtlasIdAppBrandingInput branding;
    public String type;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreateAtlasIdAppBody() {
    }

    public CreateAtlasIdAppBody name(String v) { this.name = v; return this; }
    public CreateAtlasIdAppBody redirectUris(List<String> v) { this.redirectUris = v; return this; }
    public CreateAtlasIdAppBody postLogoutRedirectUris(List<String> v) { this.postLogoutRedirectUris = v; return this; }
    public CreateAtlasIdAppBody backchannelLogoutUri(String v) { this.backchannelLogoutUri = v; return this; }
    public CreateAtlasIdAppBody backchannelLogoutSessionRequired(Boolean v) { this.backchannelLogoutSessionRequired = v; return this; }
    public CreateAtlasIdAppBody frontchannelLogoutUri(String v) { this.frontchannelLogoutUri = v; return this; }
    public CreateAtlasIdAppBody frontchannelLogoutSessionRequired(Boolean v) { this.frontchannelLogoutSessionRequired = v; return this; }
    public CreateAtlasIdAppBody logoUrl(String v) { this.logoUrl = v; return this; }
    public CreateAtlasIdAppBody homepageUrl(String v) { this.homepageUrl = v; return this; }
    public CreateAtlasIdAppBody allowedScopes(List<String> v) { this.allowedScopes = v; return this; }
    public CreateAtlasIdAppBody branding(AtlasIdAppBrandingInput v) { this.branding = v; return this; }
    public CreateAtlasIdAppBody type(String v) { this.type = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CreateAtlasIdAppBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
