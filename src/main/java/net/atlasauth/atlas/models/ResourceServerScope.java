package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One scope a resource server (API) exposes. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResourceServerScope(
        String value,
        String description) {
}
