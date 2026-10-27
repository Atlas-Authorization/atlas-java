package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/policy_bundles}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicyBundlesListResponse(
        String object,
        List<PolicyBundlesListResponseDataItem> data,
        Boolean hasMore) {
}
