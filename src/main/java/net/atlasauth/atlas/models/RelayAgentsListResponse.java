package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/relay_agents}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RelayAgentsListResponse(
        String object,
        List<RelayAgentsListResponseDataItem> data,
        Boolean hasMore) {
}
