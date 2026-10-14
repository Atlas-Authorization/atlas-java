package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/** How the Sign in with Atlas page and consent screen look for this app. On PATCH each field merges with what is stored; a field set to null clears it, and branding: null clears all of them. The Atlas header and the app domain are always shown. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class AtlasIdAppBrandingInput {
    /** Default auto (follows the visitor’s system setting). */
    public String theme;
    /** Button colour, #RRGGBB. The button text is black or white, whichever contrasts more. */
    public String accentColor;
    /** Page background behind the card, #RRGGBB. */
    public String backgroundColor;
    /** https URL, linked in the page footer. */
    public String privacyPolicyUrl;
    /** https URL, linked in the page footer. */
    public String termsOfServiceUrl;
    /** The sign-in page design (AtlasDesign settings). Merged like a design PATCH over what is stored (null inside removes a setting); design: null removes it. When no design is set, theme/accent_color/background_color are used. logoPlacement "hidden" is refused; images must be design assets uploaded… */
    public AtlasDesign design;
    /** Page text. Plain text only (shown as typed, never as HTML), one line, no control characters; empty or null uses the default. `{app}` is replaced with the app name. Merged per field on PATCH. */
    public AtlasIdAppCopy copy;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AtlasIdAppBrandingInput() {
    }

    public AtlasIdAppBrandingInput theme(String v) { this.theme = v; return this; }
    public AtlasIdAppBrandingInput accentColor(String v) { this.accentColor = v; return this; }
    public AtlasIdAppBrandingInput backgroundColor(String v) { this.backgroundColor = v; return this; }
    public AtlasIdAppBrandingInput privacyPolicyUrl(String v) { this.privacyPolicyUrl = v; return this; }
    public AtlasIdAppBrandingInput termsOfServiceUrl(String v) { this.termsOfServiceUrl = v; return this; }
    public AtlasIdAppBrandingInput design(AtlasDesign v) { this.design = v; return this; }
    public AtlasIdAppBrandingInput copy(AtlasIdAppCopy v) { this.copy = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AtlasIdAppBrandingInput set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
