package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ReportBillingUsageBody {
    public String subjectType;
    public String subjectId;
    public Double quantity;
    public String idempotencyKey;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public ReportBillingUsageBody() {
    }

    public ReportBillingUsageBody subjectType(String v) { this.subjectType = v; return this; }
    public ReportBillingUsageBody subjectId(String v) { this.subjectId = v; return this; }
    public ReportBillingUsageBody quantity(Double v) { this.quantity = v; return this; }
    public ReportBillingUsageBody idempotencyKey(String v) { this.idempotencyKey = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public ReportBillingUsageBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
