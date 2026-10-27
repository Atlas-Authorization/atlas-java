package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/listen/sessions}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ListenCreateSessionResponse(
        String object,
        String id,
        String secret,
        List<String> events,
        Long expiresAt,
        String streamUrl) {
}
