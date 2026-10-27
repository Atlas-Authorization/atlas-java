package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/iga/users/{id}/offboard}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaLifecycleOffboardResponse(
        String object,
        String userId,
        Long revokedGrants,
        Long cancelledRequests,
        Object accountClosure) {
}
