package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OpenFgaBatchCheckBody {
    public List<OpenFgaBatchCheckItem> checks;
    public String authorizationModelId;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public OpenFgaBatchCheckBody() {
    }

    public OpenFgaBatchCheckBody checks(List<OpenFgaBatchCheckItem> v) { this.checks = v; return this; }
    public OpenFgaBatchCheckBody authorizationModelId(String v) { this.authorizationModelId = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public OpenFgaBatchCheckBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
