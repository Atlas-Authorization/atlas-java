package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AtlasDesignPalette {
    public String primary;
    @JsonProperty("primaryInk") public String primaryInk;
    public String accent;
    public String background;
    public String surface;
    public String text;
    public String muted;
    public String border;
    public String danger;
    public String success;
    @JsonProperty("inputBackground") public String inputBackground;
    @JsonProperty("inputText") public String inputText;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AtlasDesignPalette() {
    }

    public AtlasDesignPalette primary(String v) { this.primary = v; return this; }
    public AtlasDesignPalette primaryInk(String v) { this.primaryInk = v; return this; }
    public AtlasDesignPalette accent(String v) { this.accent = v; return this; }
    public AtlasDesignPalette background(String v) { this.background = v; return this; }
    public AtlasDesignPalette surface(String v) { this.surface = v; return this; }
    public AtlasDesignPalette text(String v) { this.text = v; return this; }
    public AtlasDesignPalette muted(String v) { this.muted = v; return this; }
    public AtlasDesignPalette border(String v) { this.border = v; return this; }
    public AtlasDesignPalette danger(String v) { this.danger = v; return this; }
    public AtlasDesignPalette success(String v) { this.success = v; return this; }
    public AtlasDesignPalette inputBackground(String v) { this.inputBackground = v; return this; }
    public AtlasDesignPalette inputText(String v) { this.inputText = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AtlasDesignPalette set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
