package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/** The fully-resolved design every surface renders (defaults ← preset ← overrides): the same shape as AtlasDesign with every field present. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResolvedDesign(
        long version,
        String preset,
        @JsonProperty("colorScheme") String colorScheme,
        AtlasDesignPalette palette,
        @JsonProperty("paletteDark") AtlasDesignPalette paletteDark,
        AtlasDesignTypography typography,
        AtlasDesignShape shape,
        AtlasDesignElevation elevation,
        AtlasDesignSpacing spacing,
        AtlasDesignCard card,
        AtlasDesignLayout layout,
        AtlasDesignButtons buttons,
        AtlasDesignInputs inputs,
        AtlasDesignBackground background,
        @JsonProperty("backgroundDark") AtlasDesignBackground backgroundDark,
        AtlasDesignLogo logo) {
}
