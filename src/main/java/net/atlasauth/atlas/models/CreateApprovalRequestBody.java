package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateApprovalRequestBody {
    /** What is being approved, shown on the device (e.g. "Remote desktop connection from 203.0.113.9, Berlin"). */
    public String description;
    /** Optional key/value details to render beneath the description. String values only. */
    public Map<String, String> context;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreateApprovalRequestBody() {
    }

    public CreateApprovalRequestBody description(String v) { this.description = v; return this; }
    public CreateApprovalRequestBody context(Map<String, String> v) { this.context = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CreateApprovalRequestBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
