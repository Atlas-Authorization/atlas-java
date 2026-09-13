package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The result of an FGA {@code batch-check} — one decision per input item. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaBatchCheckResult(
        String object,
        String authorizationModelId,
        List<FgaBatchCheckResultItem> result) {
}
