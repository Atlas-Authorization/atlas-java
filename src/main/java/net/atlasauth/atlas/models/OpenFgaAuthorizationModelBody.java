package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OpenFgaAuthorizationModelBody {
    public List<Map<String, Object>> typeDefinitions;
    public String schemaVersion;
    public String dsl;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public OpenFgaAuthorizationModelBody() {
    }

    public OpenFgaAuthorizationModelBody typeDefinitions(List<Map<String, Object>> v) { this.typeDefinitions = v; return this; }
    public OpenFgaAuthorizationModelBody schemaVersion(String v) { this.schemaVersion = v; return this; }
    public OpenFgaAuthorizationModelBody dsl(String v) { this.dsl = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public OpenFgaAuthorizationModelBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
