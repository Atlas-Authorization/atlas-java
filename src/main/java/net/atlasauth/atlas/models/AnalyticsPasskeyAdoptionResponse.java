package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/analytics/passkey_adoption}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalyticsPasskeyAdoptionResponse(
        String object,
        AnalyticsFunnelResponseWindow window,
        Long users,
        Long usersWithPasskey,
        Double adoptionRate,
        List<AnalyticsPasskeyAdoptionResponseDataItem> data) {
}
