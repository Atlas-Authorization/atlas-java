package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RecordConsentBody {
    public String policy;
    public String version;
    public String status;
    public String source;
    public Map<String, Object> metadata;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public RecordConsentBody() {
    }

    public RecordConsentBody policy(String v) { this.policy = v; return this; }
    public RecordConsentBody version(String v) { this.version = v; return this; }
    public RecordConsentBody status(String v) { this.status = v; return this; }
    public RecordConsentBody source(String v) { this.source = v; return this; }
    public RecordConsentBody metadata(Map<String, Object> v) { this.metadata = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public RecordConsentBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
