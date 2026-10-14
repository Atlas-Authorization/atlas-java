package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateOrganizationAuditStreamBody {
    /** Public HTTPS endpoint. SSRF-validated at save and at delivery. */
    public String url;
    /** Shared HMAC-SHA256 signing secret (min 16 chars). Write-only; stored encrypted and never returned. */
    public String secret;
    /** Optional shape hint for the collector. Default `json`. */
    public String format;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreateOrganizationAuditStreamBody() {
    }

    public CreateOrganizationAuditStreamBody url(String v) { this.url = v; return this; }
    public CreateOrganizationAuditStreamBody secret(String v) { this.secret = v; return this; }
    public CreateOrganizationAuditStreamBody format(String v) { this.format = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CreateOrganizationAuditStreamBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
