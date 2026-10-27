package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsAppSettingsGetResponseRisk(
        Boolean enabled,
        Object mode,
        Object stepUpThreshold,
        Object onHighRiskNoFactor,
        Map<String, Object> levelActions,
        Map<String, Object> midSession) {
}
