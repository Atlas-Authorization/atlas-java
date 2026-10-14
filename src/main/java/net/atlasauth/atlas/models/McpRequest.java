package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class McpRequest {
    public String jsonrpc;
    public Object id;
    public String method;
    public Object params;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public McpRequest() {
    }

    public McpRequest jsonrpc(String v) { this.jsonrpc = v; return this; }
    public McpRequest id(Object v) { this.id = v; return this; }
    public McpRequest method(String v) { this.method = v; return this; }
    public McpRequest params(Object v) { this.params = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public McpRequest set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
