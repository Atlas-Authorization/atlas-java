package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AtlasDesignCard {
    public String style;
    public Long blur;
    public String width;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AtlasDesignCard() {
    }

    public AtlasDesignCard style(String v) { this.style = v; return this; }
    public AtlasDesignCard blur(Long v) { this.blur = v; return this; }
    public AtlasDesignCard width(String v) { this.width = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AtlasDesignCard set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
