package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/connections/{id}/webhooks/verify}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsWebhooksVerifyResponse(
        String object,
        String connectionId,
        Object scheme,
        Boolean valid,
        String reason,
        Boolean replayed,
        Long timestamp,
        String deliveryId,
        Map<String, Object> handshake) {
}
