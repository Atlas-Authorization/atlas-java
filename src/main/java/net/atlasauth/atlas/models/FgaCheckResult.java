package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The result of an FGA {@code check} — a single allow/deny decision. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaCheckResult(
        String object,
        boolean allowed,
        String authorizationModelId) {
}
