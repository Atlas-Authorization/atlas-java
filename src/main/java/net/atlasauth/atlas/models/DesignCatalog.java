package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The public design-system catalog: every sign-in page preset (fully resolved),
 * the curated font list, and the documented {@code --atlas-*} CSS variables.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DesignCatalog(
        String object,
        long version,
        List<Preset> presets,
        List<Font> fonts,
        List<CssVariable> cssVariables) {

    /** One preset: its id (use it as {@code AtlasDesign.preset}) and the design it resolves to. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Preset(String id, String label, String description, ResolvedDesign resolved) {
    }

    /** A curated font. {@code stylesheet} is a path on the API origin, or null for system fonts. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Font(String id, String label, String family, String category, boolean webFont, String stylesheet) {
    }

    /** A documented {@code --atlas-*} CSS custom property. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CssVariable(
            String name,
            String type,
            @JsonProperty("perMode") Boolean perMode,
            String description) {
    }
}
