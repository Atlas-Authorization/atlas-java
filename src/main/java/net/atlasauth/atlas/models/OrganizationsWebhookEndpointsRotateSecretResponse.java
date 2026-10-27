package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/organizations/{id}/webhook_endpoints/{endpointId}/rotate_secret}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrganizationsWebhookEndpointsRotateSecretResponse(
        String object,
        String secret,
        Long overlapSeconds,
        Long previousSecretExpiresAt) {
}
