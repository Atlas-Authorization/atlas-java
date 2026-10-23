package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** A profile mapping run against a sample: the mapped {@code profile}, a per-target {@code trace} ({@code value}, {@code source}) and {@code warnings}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfileMappingPreview(
        String object,
        Map<String, Object> profile,
        Map<String, Map<String, Object>> trace,
        List<String> warnings) {
}
