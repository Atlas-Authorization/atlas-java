package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** Create reveals the signing secret ({@code whsec_...}) exactly once. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookEndpointWithSecret(
        String object,
        String id,
        String url,
        List<String> enabledEvents,
        boolean active,
        Long disabledAt,
        long createdAt,
        String secret) {
}
