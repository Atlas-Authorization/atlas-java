package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/delivery_health/{kind}/{id}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryHealthGetResponse(
        String object,
        String kind,
        String targetId,
        String url,
        String label,
        String state,
        Long failingSince,
        Long disabledAt,
        Long lastSuccessAt,
        Map<String, Object> lastFailure,
        Long consecutiveFailures,
        @JsonProperty("success_rate_24h") Double successRate24h,
        @JsonProperty("attempts_24h") Map<String, Object> attempts24h,
        List<String> alertContacts,
        Map<String, Object> alerts) {
}
