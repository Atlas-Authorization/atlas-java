package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

/** The result of an FGA {@code expand} — the full userset {@code tree}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaExpandResult(
        String object,
        JsonNode tree,
        String authorizationModelId) {
}
