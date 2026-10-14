package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class MintApiKeyTokenBody {
    public String secret;
    public Double ttlSeconds;
    public Object audience;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public MintApiKeyTokenBody() {
    }

    public MintApiKeyTokenBody secret(String v) { this.secret = v; return this; }
    public MintApiKeyTokenBody ttlSeconds(Double v) { this.ttlSeconds = v; return this; }
    public MintApiKeyTokenBody audience(Object v) { this.audience = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public MintApiKeyTokenBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
