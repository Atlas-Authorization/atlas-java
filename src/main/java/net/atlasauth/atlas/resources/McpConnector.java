package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.McpConnectorBody;
import net.atlasauth.atlas.models.McpConnectorConfig;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The instance's remote MCP connector ({@code /v1/mcp_connector}). */
public final class McpConnector {

    private final Transport t;

    public McpConnector(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/mcp_connector} — Read the instance's remote MCP connector: enabled state, exposed tool scopes, dynamic-registration policy, plus the computed discovery URL, resource identifier and whether the resource server exists. */
    public McpConnectorConfig get() {
        return t.get("/v1/mcp_connector", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/mcp_connector} — Configure the remote MCP connector (enabled, name, exposed_scopes, dynamic_registration, allowed_redirect_hosts). */
    public McpConnectorConfig replace(McpConnectorBody body) {
        return t.put("/v1/mcp_connector", body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/mcp_connector} — Disable the remote MCP connector. */
    public McpConnectorConfig disable() {
        return t.delete("/v1/mcp_connector", new TypeReference<>() {
        });
    }
}
