package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record McpServer(
        String object,
        String protocolVersion,
        McpServerServer server,
        Map<String, Object> capabilities,
        List<Map<String, Object>> tools,
        JsonNode rateLimit,
        List<String> recommendedAgentScopes) {
}
