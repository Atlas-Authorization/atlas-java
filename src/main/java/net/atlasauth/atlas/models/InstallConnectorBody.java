package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class InstallConnectorBody {
    public String shareMode;
    public String clientId;
    public String clientSecret;
    public List<String> scopes;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public InstallConnectorBody() {
    }

    public InstallConnectorBody shareMode(String v) { this.shareMode = v; return this; }
    public InstallConnectorBody clientId(String v) { this.clientId = v; return this; }
    public InstallConnectorBody clientSecret(String v) { this.clientSecret = v; return this; }
    public InstallConnectorBody scopes(List<String> v) { this.scopes = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public InstallConnectorBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
