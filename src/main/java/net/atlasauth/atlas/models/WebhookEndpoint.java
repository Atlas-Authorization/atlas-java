package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookEndpoint(
        String object,
        String id,
        String url,
        List<String> enabledEvents,
        boolean active,
        Long disabledAt,
        long createdAt,
        /** Epoch ms since deliveries started failing, or null while healthy. */
        Long failingSince,
        /** While a secret rotation overlap is open: when the previous secret stops signing deliveries (epoch ms); else null. */
        Long previousSecretExpiresAt,
        /** The HTTPS receiver, or a descriptor such as {@code aws-eventbridge://us-east-1/default}. */
        String description,
        /** Which names {@code "*"} delivers under: {@code canonical} or {@code legacy}. */
        String eventNaming,
        /** A third-party receiver: preflight does not flag its host. */
        Boolean external,
        /** The signature versions sent: {@code v1}, and {@code v2} (which also covers the URL). */
        List<String> signatureVersions,
        /** Where deliveries go; the credentials are never returned ({@code credentials_set} says whether they are stored). */
        JsonNode destination,
        /** healthy, failing or disabled. */
        String health,
        Long lastSuccessAt,
        /** The last failed attempt: {@code at}, {@code status}, {@code error}. */
        JsonNode lastFailure,
        Integer consecutiveFailures,
        /** The share of attempts in the last 24 hours that succeeded (0-1), null with none. */
        @JsonProperty("success_rate_24h") Double successRate24h,
        /** {@code succeeded} and {@code failed} counts of the last 24 hours. */
        @JsonProperty("attempts_24h") JsonNode attempts24h,
        /** Who is emailed when it fails; null = the account's owners. */
        List<String> alertContacts,
        /** first_sent_at, second_sent_at, disabled_sent_at. */
        JsonNode alertsSent,
        /** When a failing endpoint is switched off. */
        JsonNode autoDisable,
        /** Set on an update that re-enabled the endpoint: the replay call that re-sends what it missed ({@code since}, {@code method}, {@code path}, {@code note}). */
        JsonNode replay,
        /** On create / upsert: whether this call created it. */
        Boolean created,
        Long updatedAt,
        /** Provisioning tags. */
        Map<String, Object> metadata,
        /** Your idempotent-create key. */
        String externalRef,
        /** True on a create that sent an {@code external_ref} and got the existing endpoint back. */
        Boolean idempotentReplay,
        /** When the most recent failure streak began (epoch ms); kept after the endpoint recovers or is re-enabled, so since=failing_since still resolves on replay. */ Long lastFailingSince) {
    /** The shape before lastFailingSince. */
    public WebhookEndpoint(String object, String id, String url, List<String> enabledEvents, boolean active, Long disabledAt, long createdAt, Long failingSince, Long previousSecretExpiresAt, String description, String eventNaming, Boolean external, List<String> signatureVersions, JsonNode destination, String health, Long lastSuccessAt, JsonNode lastFailure, Integer consecutiveFailures, Double successRate24h, JsonNode attempts24h, List<String> alertContacts, JsonNode alertsSent, JsonNode autoDisable, JsonNode replay, Boolean created, Long updatedAt, Map<String, Object> metadata, String externalRef, Boolean idempotentReplay) {
        this(object, id, url, enabledEvents, active, disabledAt, createdAt, failingSince, previousSecretExpiresAt, description, eventNaming, external, signatureVersions, destination, health, lastSuccessAt, lastFailure, consecutiveFailures, successRate24h, attempts24h, alertContacts, alertsSent, autoDisable, replay, created, updatedAt, metadata, externalRef, idempotentReplay, null);
    }


    /** The 1.13 shape, without the health, destination and provisioning fields. */
    public WebhookEndpoint(String object, String id, String url, List<String> enabledEvents, boolean active, Long disabledAt, long createdAt, Long failingSince, Long previousSecretExpiresAt) {
        this(object, id, url, enabledEvents, active, disabledAt, createdAt, failingSince, previousSecretExpiresAt,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    /** The 1.5 shape, without {@code previousSecretExpiresAt}. */
    public WebhookEndpoint(String object, String id, String url, List<String> enabledEvents, boolean active, Long disabledAt, long createdAt, Long failingSince) {
        this(object, id, url, enabledEvents, active, disabledAt, createdAt, failingSince, null);
    }
}
