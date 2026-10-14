package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AtlasDesignShape {
    public Long radius;
    @JsonProperty("controlRadius") public Long controlRadius;
    @JsonProperty("borderWidth") public Long borderWidth;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AtlasDesignShape() {
    }

    public AtlasDesignShape radius(Long v) { this.radius = v; return this; }
    public AtlasDesignShape controlRadius(Long v) { this.controlRadius = v; return this; }
    public AtlasDesignShape borderWidth(Long v) { this.borderWidth = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AtlasDesignShape set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
