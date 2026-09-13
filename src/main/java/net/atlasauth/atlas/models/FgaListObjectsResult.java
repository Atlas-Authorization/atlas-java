package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The result of an FGA {@code list-objects} — the objects a user relates to. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaListObjectsResult(
        String object,
        List<String> objects,
        String authorizationModelId) {
}
