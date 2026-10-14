package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class AddOrganizationEndUserBody {
    /** An instance user id to attach as a reader. */
    public String userId;
    /** Defaults to active. Use pending when the org gates readers behind approval. */
    public String status;
    /** How the reader entered the pool. Defaults to token_exchange for this API. */
    public String source;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AddOrganizationEndUserBody() {
    }

    public AddOrganizationEndUserBody userId(String v) { this.userId = v; return this; }
    public AddOrganizationEndUserBody status(String v) { this.status = v; return this; }
    public AddOrganizationEndUserBody source(String v) { this.source = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AddOrganizationEndUserBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
