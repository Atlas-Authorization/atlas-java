package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/users/{id}/required_actions}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersRequiredActionsRequestResponse(
        String object,
        String id,
        String userId,
        String action,
        String url,
        String reason,
        String createdBy,
        Long createdAt) {
}
