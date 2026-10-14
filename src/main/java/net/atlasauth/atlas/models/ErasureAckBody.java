package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ErasureAckBody {
    /** The acknowledging app/system (its app id or a label). */
    public String source;
    /** Alias of source. */
    public String appId;
    /** The app's contributed data for the DSAR export; omit to only confirm a purge. */
    public Map<String, Object> payload;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public ErasureAckBody() {
    }

    public ErasureAckBody source(String v) { this.source = v; return this; }
    public ErasureAckBody appId(String v) { this.appId = v; return this; }
    public ErasureAckBody payload(Map<String, Object> v) { this.payload = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public ErasureAckBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
