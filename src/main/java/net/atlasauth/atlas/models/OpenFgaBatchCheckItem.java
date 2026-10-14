package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OpenFgaBatchCheckItem {
    public OpenFgaTupleKey tupleKey;
    public String correlationId;
    public Map<String, Object> contextualTuples;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public OpenFgaBatchCheckItem() {
    }

    public OpenFgaBatchCheckItem tupleKey(OpenFgaTupleKey v) { this.tupleKey = v; return this; }
    public OpenFgaBatchCheckItem correlationId(String v) { this.correlationId = v; return this; }
    public OpenFgaBatchCheckItem contextualTuples(Map<String, Object> v) { this.contextualTuples = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public OpenFgaBatchCheckItem set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
