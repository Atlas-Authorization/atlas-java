package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OrganizationBrandingBody {
    public String displayName;
    public String logoUrl;
    public String primaryColor;
    public String supportUrl;
    public String customDomain;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public OrganizationBrandingBody() {
    }

    public OrganizationBrandingBody displayName(String v) { this.displayName = v; return this; }
    public OrganizationBrandingBody logoUrl(String v) { this.logoUrl = v; return this; }
    public OrganizationBrandingBody primaryColor(String v) { this.primaryColor = v; return this; }
    public OrganizationBrandingBody supportUrl(String v) { this.supportUrl = v; return this; }
    public OrganizationBrandingBody customDomain(String v) { this.customDomain = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public OrganizationBrandingBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
