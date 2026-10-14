package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OpenFgaListObjectsBody {
    /** e.g. document */
    public String type;
    /** e.g. viewer */
    public String relation;
    /** e.g. user:alice */
    public String user;
    public String authorizationModelId;
    /** OpenFGA contextual tuples. */
    public Map<String, Object> contextualTuples;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public OpenFgaListObjectsBody() {
    }

    public OpenFgaListObjectsBody type(String v) { this.type = v; return this; }
    public OpenFgaListObjectsBody relation(String v) { this.relation = v; return this; }
    public OpenFgaListObjectsBody user(String v) { this.user = v; return this; }
    public OpenFgaListObjectsBody authorizationModelId(String v) { this.authorizationModelId = v; return this; }
    public OpenFgaListObjectsBody contextualTuples(Map<String, Object> v) { this.contextualTuples = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public OpenFgaListObjectsBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
