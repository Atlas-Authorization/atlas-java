package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The object a config change touched. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConfigChangelogTarget(
        String type,
        String id) {
}
