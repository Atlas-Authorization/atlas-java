package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.McpRequest;
import net.atlasauth.atlas.models.McpResponse;
import net.atlasauth.atlas.models.McpServer;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The Atlas MCP server endpoint ({@code /v1/mcp}). */
public final class Mcp {

    private final Transport t;

    public Mcp(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/mcp} — MCP server descriptor and the tools this key may call. */
    public McpServer describe() {
        return t.get("/v1/mcp", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/mcp} — MCP JSON-RPC endpoint: initialize, tools/list, tools/call, ping. */
    public McpResponse call(McpRequest body) {
        return t.post("/v1/mcp", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public McpResponse call(McpRequest body, String idempotencyKey) {
        return t.post("/v1/mcp", body, idempotencyKey, new TypeReference<>() {
        });
    }
}
