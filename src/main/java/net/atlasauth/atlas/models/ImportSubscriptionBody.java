package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ImportSubscriptionBody {
    public String subjectType;
    public String subjectId;
    public String stripeSubscriptionId;
    public String stripeCustomerId;
    public String planId;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public ImportSubscriptionBody() {
    }

    public ImportSubscriptionBody subjectType(String v) { this.subjectType = v; return this; }
    public ImportSubscriptionBody subjectId(String v) { this.subjectId = v; return this; }
    public ImportSubscriptionBody stripeSubscriptionId(String v) { this.stripeSubscriptionId = v; return this; }
    public ImportSubscriptionBody stripeCustomerId(String v) { this.stripeCustomerId = v; return this; }
    public ImportSubscriptionBody planId(String v) { this.planId = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public ImportSubscriptionBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
