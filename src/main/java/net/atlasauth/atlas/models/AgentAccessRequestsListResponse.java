package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/agent_access_requests}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AgentAccessRequestsListResponse(
        String object,
        List<Map<String, Object>> data,
        Boolean hasMore) {
}
