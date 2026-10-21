package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

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
        Long previousSecretExpiresAt) {

    /** The 1.5 shape, without {@code previousSecretExpiresAt}. */
    public WebhookEndpoint(String object, String id, String url, List<String> enabledEvents, boolean active, Long disabledAt, long createdAt, Long failingSince) {
        this(object, id, url, enabledEvents, active, disabledAt, createdAt, failingSince, null);
    }
}
