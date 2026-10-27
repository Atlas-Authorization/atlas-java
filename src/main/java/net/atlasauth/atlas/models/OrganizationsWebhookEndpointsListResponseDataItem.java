package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationsWebhookEndpointsListResponseDataItem(
        String object,
        String id,
        String url,
        String description,
        List<String> enabledEvents,
        String eventNaming,
        Boolean active,
        Boolean external,
        List<String> signatureVersions,
        Map<String, Object> destination,
        String health,
        Long failingSince,
        Long lastFailingSince,
        Long disabledAt,
        Long lastSuccessAt,
        Map<String, Object> lastFailure,
        Long consecutiveFailures,
        @JsonProperty("success_rate_24h") Double successRate24h,
        @JsonProperty("attempts_24h") Map<String, Object> attempts24h,
        List<String> alertContacts,
        Map<String, Object> alertsSent,
        Map<String, Object> autoDisable,
        Long previousSecretExpiresAt,
        Map<String, Object> replay,
        Boolean created,
        Long createdAt,
        Long updatedAt) {
}
