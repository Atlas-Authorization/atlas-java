package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AtlasDesignBackground {
    public String color;
    public AtlasDesignBackgroundGradient gradient;
    public AtlasDesignBackgroundImage image;
    public String pattern;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AtlasDesignBackground() {
    }

    public AtlasDesignBackground color(String v) { this.color = v; return this; }
    public AtlasDesignBackground gradient(AtlasDesignBackgroundGradient v) { this.gradient = v; return this; }
    public AtlasDesignBackground image(AtlasDesignBackgroundImage v) { this.image = v; return this; }
    public AtlasDesignBackground pattern(String v) { this.pattern = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AtlasDesignBackground set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
