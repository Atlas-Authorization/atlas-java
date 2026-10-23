package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A policy version as referenced from a {@link Policy}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicyVersionRef(
        String id,
        String version,
        String status,
        String contentSha256,
        Long publishedAt,
        Long effectiveAt) {
}
