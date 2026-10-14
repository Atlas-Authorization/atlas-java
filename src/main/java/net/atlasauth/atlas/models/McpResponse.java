package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

/** JSON-RPC 2.0 envelope. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record McpResponse(
        String jsonrpc,
        JsonNode id,
        Map<String, Object> result,
        McpError error) {
}
