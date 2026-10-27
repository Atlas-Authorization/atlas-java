package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/organizations/{id}/webhook_endpoints/{endpointId}/deliveries}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationsWebhookEndpointsDeliveriesResponse(
        String object,
        List<OrganizationsWebhookEndpointsDeliveriesResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
