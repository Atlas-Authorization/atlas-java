package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookDeliveryQueued(
        String object,
        String id,
        String eventId,
        String status,
        String note) {
}
