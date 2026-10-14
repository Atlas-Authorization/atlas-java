package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AtlasDesignLayout {
    public String position;
    @JsonProperty("splitImageSide") public String splitImageSide;
    @JsonProperty("logoPlacement") public String logoPlacement;
    @JsonProperty("logoSize") public String logoSize;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AtlasDesignLayout() {
    }

    public AtlasDesignLayout position(String v) { this.position = v; return this; }
    public AtlasDesignLayout splitImageSide(String v) { this.splitImageSide = v; return this; }
    public AtlasDesignLayout logoPlacement(String v) { this.logoPlacement = v; return this; }
    public AtlasDesignLayout logoSize(String v) { this.logoSize = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AtlasDesignLayout set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
