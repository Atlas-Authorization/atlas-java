package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ProgressionEventBody {
    public String track;
    public Double amount;
    public String reason;
    public String idempotencyKey;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public ProgressionEventBody() {
    }

    public ProgressionEventBody track(String v) { this.track = v; return this; }
    public ProgressionEventBody amount(Double v) { this.amount = v; return this; }
    public ProgressionEventBody reason(String v) { this.reason = v; return this; }
    public ProgressionEventBody idempotencyKey(String v) { this.idempotencyKey = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public ProgressionEventBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
