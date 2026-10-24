package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Create reveals the signing secret ({@code whsec_...}) exactly once. An upsert by URL returns the same
 * shape: {@code secret} is set only when the call created the endpoint ({@code created}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookEndpointWithSecret(
        String object,
        String id,
        String url,
        List<String> enabledEvents,
        boolean active,
        Long disabledAt,
        long createdAt,
        String secret,
        /** Epoch ms since deliveries started failing, or null while healthy. */
        Long failingSince,
        String description,
        String eventNaming,
        Boolean external,
        List<String> signatureVersions,
        JsonNode destination,
        List<String> alertContacts,
        /** On create / upsert: whether this call created it. */
        Boolean created,
        Map<String, Object> metadata,
        String externalRef,
        Boolean idempotentReplay) {

    /** The previous shape, without the destination, naming and provisioning fields. */
    public WebhookEndpointWithSecret(String object, String id, String url, List<String> enabledEvents, boolean active, Long disabledAt, long createdAt, String secret, Long failingSince) {
        this(object, id, url, enabledEvents, active, disabledAt, createdAt, secret, failingSince,
                null, null, null, null, null, null, null, null, null, null);
    }
}
