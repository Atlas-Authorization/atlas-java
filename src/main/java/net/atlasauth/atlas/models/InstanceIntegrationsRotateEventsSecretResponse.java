package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/instance/integrations/{product}/events_secret/rotate}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InstanceIntegrationsRotateEventsSecretResponse(
        String object,
        String integration,
        String endpointId,
        String url,
        String secret,
        Long overlapSeconds,
        Long previousSecretExpiresAt,
        String warning) {
}
