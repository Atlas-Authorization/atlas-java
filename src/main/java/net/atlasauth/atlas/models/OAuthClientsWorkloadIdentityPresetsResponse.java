package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/workload_identity/presets}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsWorkloadIdentityPresetsResponse(
        String object,
        List<OAuthClientsWorkloadIdentityPresetsResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
