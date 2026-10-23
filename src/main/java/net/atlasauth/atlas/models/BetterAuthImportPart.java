package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** An uploaded part of a sealed Better Auth bundle. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BetterAuthImportPart(String object, String importId, Long part, Long bytes, String sha256) {
}
