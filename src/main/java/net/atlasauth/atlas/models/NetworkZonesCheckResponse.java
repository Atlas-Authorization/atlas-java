package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/network_zones/{id}/check}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NetworkZonesCheckResponse(
        String object,
        String zoneId,
        String ip,
        Boolean matches,
        NetworkZonesCheckResponseFacts facts) {
}
