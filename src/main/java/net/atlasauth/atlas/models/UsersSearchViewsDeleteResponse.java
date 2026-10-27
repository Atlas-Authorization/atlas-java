package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/users/search/views/{id}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersSearchViewsDeleteResponse(
        String object,
        String id,
        Boolean deleted) {
}
