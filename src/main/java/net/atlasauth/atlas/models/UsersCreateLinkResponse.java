package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/users/{id}/links}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersCreateLinkResponse(
        String object,
        String type,
        String userId,
        String emailAddressId,
        String emailAddress,
        String url,
        String code,
        String attemptId,
        Long expiresAt) {
}
