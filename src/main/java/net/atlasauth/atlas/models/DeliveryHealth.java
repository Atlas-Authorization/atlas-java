package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** The delivery health of one outbound target (a webhook endpoint, the referral connection, a back-channel receiver or an integration). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryHealth(
        String object,
        /** webhook_endpoint, referral_connection, backchannel_receiver or integration. */ String kind,
        String targetId,
        String url,
        String label,
        /** healthy, failing or disabled. */ String state,
        Long failingSince,
        Long disabledAt,
        Long lastSuccessAt,
        /** {@code at}, {@code status}, {@code error}. */ JsonNode lastFailure,
        int consecutiveFailures,
        @JsonProperty("success_rate_24h") Double successRate24h,
        @JsonProperty("attempts_24h") JsonNode attempts24h,
        List<String> alertContacts,
        /** first_sent_at, second_sent_at, disabled_sent_at. */ JsonNode alerts) {
}
