package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class McpConnectorBody {
    /** Master switch for the connector and its OAuth discovery. */
    public Boolean enabled;
    /** Display name shown on the consent screen (max 80 chars). */
    public String name;
    /** Which user-scoped tool groups the connector exposes. */
    public List<String> exposedScopes;
    /** Let MCP clients self-register an OAuth client (RFC 7591) for the MCP resource. */
    public Boolean dynamicRegistration;
    /** Extra redirect-URI hostnames allowed for dynamic registration beyond the built-in MCP clients. */
    public List<String> allowedRedirectHosts;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public McpConnectorBody() {
    }

    public McpConnectorBody enabled(Boolean v) { this.enabled = v; return this; }
    public McpConnectorBody name(String v) { this.name = v; return this; }
    public McpConnectorBody exposedScopes(List<String> v) { this.exposedScopes = v; return this; }
    public McpConnectorBody dynamicRegistration(Boolean v) { this.dynamicRegistration = v; return this; }
    public McpConnectorBody allowedRedirectHosts(List<String> v) { this.allowedRedirectHosts = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public McpConnectorBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
