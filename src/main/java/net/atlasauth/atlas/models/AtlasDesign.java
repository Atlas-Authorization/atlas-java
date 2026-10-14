package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/** A structured sign-in page design: OVERRIDES on top of a preset (every field optional; unset fields come from the preset, then the defaults). Unknown keys are refused. Colours are #RRGGBB; images are uploaded design asset ids (dasset_…), never URLs; fonts are ids from GET /v1/design/catalog.… */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class AtlasDesign {
    public Long version;
    public String preset;
    @JsonProperty("colorScheme") public String colorScheme;
    public AtlasDesignPalette palette;
    @JsonProperty("paletteDark") public AtlasDesignPalette paletteDark;
    public AtlasDesignTypography typography;
    public AtlasDesignShape shape;
    public AtlasDesignElevation elevation;
    public AtlasDesignSpacing spacing;
    public AtlasDesignCard card;
    public AtlasDesignLayout layout;
    public AtlasDesignButtons buttons;
    public AtlasDesignInputs inputs;
    public AtlasDesignBackground background;
    @JsonProperty("backgroundDark") public AtlasDesignBackground backgroundDark;
    public AtlasDesignLogo logo;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AtlasDesign() {
    }

    public AtlasDesign version(Long v) { this.version = v; return this; }
    public AtlasDesign preset(String v) { this.preset = v; return this; }
    public AtlasDesign colorScheme(String v) { this.colorScheme = v; return this; }
    public AtlasDesign palette(AtlasDesignPalette v) { this.palette = v; return this; }
    public AtlasDesign paletteDark(AtlasDesignPalette v) { this.paletteDark = v; return this; }
    public AtlasDesign typography(AtlasDesignTypography v) { this.typography = v; return this; }
    public AtlasDesign shape(AtlasDesignShape v) { this.shape = v; return this; }
    public AtlasDesign elevation(AtlasDesignElevation v) { this.elevation = v; return this; }
    public AtlasDesign spacing(AtlasDesignSpacing v) { this.spacing = v; return this; }
    public AtlasDesign card(AtlasDesignCard v) { this.card = v; return this; }
    public AtlasDesign layout(AtlasDesignLayout v) { this.layout = v; return this; }
    public AtlasDesign buttons(AtlasDesignButtons v) { this.buttons = v; return this; }
    public AtlasDesign inputs(AtlasDesignInputs v) { this.inputs = v; return this; }
    public AtlasDesign background(AtlasDesignBackground v) { this.background = v; return this; }
    public AtlasDesign backgroundDark(AtlasDesignBackground v) { this.backgroundDark = v; return this; }
    public AtlasDesign logo(AtlasDesignLogo v) { this.logo = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AtlasDesign set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
