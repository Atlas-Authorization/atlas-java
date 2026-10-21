package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateAccountlessAppBody {
    public String name;
    public String environment;
    public List<String> allowedOrigins;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreateAccountlessAppBody() {
    }

    public CreateAccountlessAppBody name(String v) { this.name = v; return this; }
    public CreateAccountlessAppBody environment(String v) { this.environment = v; return this; }
    public CreateAccountlessAppBody allowedOrigins(List<String> v) { this.allowedOrigins = v; return this; }

    /**
     * Send {@code allowed_origins} as entries that may also declare how each origin signs in
     * ({@link AllowedOriginEntry}); replaces {@link #allowedOrigins(List)}.
     */
    public CreateAccountlessAppBody allowedOriginEntries(List<AllowedOriginEntry> v) {
        this.allowedOrigins = null;
        this.additionalFields.put("allowed_origins", v);
        return this;
    }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CreateAccountlessAppBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
