package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class OpenFgaReadBody {
    public OpenFgaTupleKey tupleKey;
    public Long pageSize;
    public String continuationToken;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public OpenFgaReadBody() {
    }

    public OpenFgaReadBody tupleKey(OpenFgaTupleKey v) { this.tupleKey = v; return this; }
    public OpenFgaReadBody pageSize(Long v) { this.pageSize = v; return this; }
    public OpenFgaReadBody continuationToken(String v) { this.continuationToken = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public OpenFgaReadBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
