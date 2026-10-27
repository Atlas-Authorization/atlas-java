package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/workload_identity/cloud_trust}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsWorkloadIdentityCloudTrustResponse(
        String object,
        String provider,
        List<String> steps,
        Map<String, Object> trustPolicy,
        Map<String, Object> providerConfig,
        String principal,
        Map<String, Object> federatedCredential,
        String tokenEndpoint) {
}
