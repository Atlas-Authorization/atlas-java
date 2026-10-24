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
        Boolean idempotentReplay,
        /** With an `X-Secret-Recipient`: the secret sealed to it (then the plaintext field is absent). */ String sealedSecret,
        /** With an `X-Secret-Recipient`: the client secret sealed to it. */ String sealedClientSecret,
        /** With an `X-Secret-Recipient`: the webhook secret sealed to it. */ String sealedWebhookSecret,
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed) {
    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public WebhookEndpointWithSecret(String object, String id, String url, List<String> enabledEvents, boolean active, Long disabledAt, long createdAt, String secret, Long failingSince, String description, String eventNaming, Boolean external, List<String> signatureVersions, JsonNode destination, List<String> alertContacts, Boolean created, Map<String, Object> metadata, String externalRef, Boolean idempotentReplay) {
        this(object, id, url, enabledEvents, active, disabledAt, createdAt, secret, failingSince, description, eventNaming, external, signatureVersions, destination, alertContacts, created, metadata, externalRef, idempotentReplay, null, null, null, null);
    }


    /** The previous shape, without the destination, naming and provisioning fields. */
    public WebhookEndpointWithSecret(String object, String id, String url, List<String> enabledEvents, boolean active, Long disabledAt, long createdAt, String secret, Long failingSince) {
        this(object, id, url, enabledEvents, active, disabledAt, createdAt, secret, failingSince,
                null, null, null, null, null, null, null, null, null, null);
    }
}
