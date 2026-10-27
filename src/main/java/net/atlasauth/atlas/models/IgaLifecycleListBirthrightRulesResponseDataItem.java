package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaLifecycleListBirthrightRulesResponseDataItem(
        String object,
        String id,
        String name,
        Map<String, Object> match,
        List<String> entitlementIds,
        Boolean revokeOnUnmatch,
        Boolean enabled,
        Long createdAt) {
}
