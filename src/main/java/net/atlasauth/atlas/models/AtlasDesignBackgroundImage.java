package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AtlasDesignBackgroundImage {
    @JsonProperty("assetId") public String assetId;
    public String fit;
    public String position;
    public AtlasDesignBackgroundImageOverlay overlay;
    public Long blur;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AtlasDesignBackgroundImage() {
    }

    public AtlasDesignBackgroundImage assetId(String v) { this.assetId = v; return this; }
    public AtlasDesignBackgroundImage fit(String v) { this.fit = v; return this; }
    public AtlasDesignBackgroundImage position(String v) { this.position = v; return this; }
    public AtlasDesignBackgroundImage overlay(AtlasDesignBackgroundImageOverlay v) { this.overlay = v; return this; }
    public AtlasDesignBackgroundImage blur(Long v) { this.blur = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AtlasDesignBackgroundImage set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
