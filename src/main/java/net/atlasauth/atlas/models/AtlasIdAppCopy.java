package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/** Page text. Plain text only (shown as typed, never as HTML), one line, no control characters; empty or null uses the default. `{app}` is replaced with the app name. Merged per field on PATCH. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class AtlasIdAppCopy {
    /** Default "Sign in to continue to {app}". */
    public String signInTitle;
    public String signInSubtitle;
    /** Default "Create your account to continue to {app}". */
    public String signUpTitle;
    public String signUpSubtitle;
    /** Shown on the consent screen above the list of permissions. */
    public String consentMessage;
    /** https URL, linked in the page footer. */
    public String supportUrl;
    /** Text of the support link. Default "Help". */
    public String supportLabel;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AtlasIdAppCopy() {
    }

    public AtlasIdAppCopy signInTitle(String v) { this.signInTitle = v; return this; }
    public AtlasIdAppCopy signInSubtitle(String v) { this.signInSubtitle = v; return this; }
    public AtlasIdAppCopy signUpTitle(String v) { this.signUpTitle = v; return this; }
    public AtlasIdAppCopy signUpSubtitle(String v) { this.signUpSubtitle = v; return this; }
    public AtlasIdAppCopy consentMessage(String v) { this.consentMessage = v; return this; }
    public AtlasIdAppCopy supportUrl(String v) { this.supportUrl = v; return this; }
    public AtlasIdAppCopy supportLabel(String v) { this.supportLabel = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AtlasIdAppCopy set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
