package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/organizations/{id}/webhook_endpoints/{endpointId}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationsWebhookEndpointsDeleteResponse(
        String object,
        String id,
        Boolean deleted) {
}
