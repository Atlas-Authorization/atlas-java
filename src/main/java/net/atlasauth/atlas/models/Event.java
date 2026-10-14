package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Event(
        String object,
        String id,
        String type,
        WebhookPayload payload,
        long createdAt) {
}
