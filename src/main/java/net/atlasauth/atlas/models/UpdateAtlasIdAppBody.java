package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class UpdateAtlasIdAppBody {
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
    /** Per-app webhook (public https): user.updated, user.deleted and grant.revoked for this app's users. The first set returns webhook_secret once; {@code set("webhook_url", null)} removes it. */
    public String webhookUrl;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateAtlasIdAppBody() {
    }

    public UpdateAtlasIdAppBody name(String v) { this.name = v; return this; }
    public UpdateAtlasIdAppBody redirectUris(List<String> v) { this.redirectUris = v; return this; }
    public UpdateAtlasIdAppBody postLogoutRedirectUris(List<String> v) { this.postLogoutRedirectUris = v; return this; }
    public UpdateAtlasIdAppBody backchannelLogoutUri(String v) { this.backchannelLogoutUri = v; return this; }
    public UpdateAtlasIdAppBody backchannelLogoutSessionRequired(Boolean v) { this.backchannelLogoutSessionRequired = v; return this; }
    public UpdateAtlasIdAppBody frontchannelLogoutUri(String v) { this.frontchannelLogoutUri = v; return this; }
    public UpdateAtlasIdAppBody frontchannelLogoutSessionRequired(Boolean v) { this.frontchannelLogoutSessionRequired = v; return this; }
    public UpdateAtlasIdAppBody logoUrl(String v) { this.logoUrl = v; return this; }
    public UpdateAtlasIdAppBody homepageUrl(String v) { this.homepageUrl = v; return this; }
    public UpdateAtlasIdAppBody allowedScopes(List<String> v) { this.allowedScopes = v; return this; }
    public UpdateAtlasIdAppBody branding(AtlasIdAppBrandingInput v) { this.branding = v; return this; }
    public UpdateAtlasIdAppBody webhookUrl(String v) { this.webhookUrl = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public UpdateAtlasIdAppBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
