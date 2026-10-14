package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AtlasDesignTypography {
    public String font;
    @JsonProperty("headingFont") public String headingFont;
    @JsonProperty("baseSize") public Long baseSize;
    @JsonProperty("headingWeight") public Long headingWeight;
    @JsonProperty("bodyWeight") public Long bodyWeight;
    @JsonProperty("letterCase") public String letterCase;
    public String scale;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AtlasDesignTypography() {
    }

    public AtlasDesignTypography font(String v) { this.font = v; return this; }
    public AtlasDesignTypography headingFont(String v) { this.headingFont = v; return this; }
    public AtlasDesignTypography baseSize(Long v) { this.baseSize = v; return this; }
    public AtlasDesignTypography headingWeight(Long v) { this.headingWeight = v; return this; }
    public AtlasDesignTypography bodyWeight(Long v) { this.bodyWeight = v; return this; }
    public AtlasDesignTypography letterCase(String v) { this.letterCase = v; return this; }
    public AtlasDesignTypography scale(String v) { this.scale = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AtlasDesignTypography set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
