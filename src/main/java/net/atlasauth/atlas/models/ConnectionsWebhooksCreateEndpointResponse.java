package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/connections/{id}/webhook_endpoints}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsWebhooksCreateEndpointResponse(
        String object,
        String id,
        String connectionId,
        Object scheme,
        String forwardUrl,
        String ingressUrl,
        Map<String, Object> options,
        Boolean hasSigningSecret,
        Boolean enabled,
        Long lastDeliveryAt,
        Long createdAt,
        Long updatedAt,
        String forwardSecret) {
}
