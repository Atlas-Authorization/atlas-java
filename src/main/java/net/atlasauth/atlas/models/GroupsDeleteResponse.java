package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/groups/{id}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GroupsDeleteResponse(
        String object,
        String id,
        Boolean deleted) {
}
