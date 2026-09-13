package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One decision in a batch-check result, matched to its request by {@code correlationId}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaBatchCheckResultItem(
        String correlationId,
        boolean allowed) {
}
