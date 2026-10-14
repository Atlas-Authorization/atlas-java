package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OpenFgaTupleKey {
    /** e.g. user:alice */
    public String user;
    /** e.g. viewer */
    public String relation;
    /** e.g. document:roadmap */
    public String object;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public OpenFgaTupleKey() {
    }

    public OpenFgaTupleKey user(String v) { this.user = v; return this; }
    public OpenFgaTupleKey relation(String v) { this.relation = v; return this; }
    public OpenFgaTupleKey object(String v) { this.object = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public OpenFgaTupleKey set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
