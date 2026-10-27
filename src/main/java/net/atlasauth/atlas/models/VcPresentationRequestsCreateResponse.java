package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/vc/presentation_requests}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VcPresentationRequestsCreateResponse(
        String object,
        String id,
        String vct,
        String format,
        List<String> iacaRoots,
        List<String> claims,
        String status,
        Map<String, Object> result,
        String error,
        String userId,
        Long expiresAt,
        String requestUri,
        String authorizationRequestUri) {
}
