package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateEntitlementOverrideBody {
    public String subjectType;
    public String subjectId;
    /** Plan slug to pin, or null to override features/limits only. */
    public String plan;
    /** Replaces (not adds to) the resolved feature set. */
    public List<String> features;
    /** Numeric limits, e.g. {"max_devices":50}. */
    public Map<String, Object> limits;
    /** Epoch ms when the override lapses; omit/null for open-ended. */
    public Long expiresAt;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreateEntitlementOverrideBody() {
    }

    public CreateEntitlementOverrideBody subjectType(String v) { this.subjectType = v; return this; }
    public CreateEntitlementOverrideBody subjectId(String v) { this.subjectId = v; return this; }
    public CreateEntitlementOverrideBody plan(String v) { this.plan = v; return this; }
    public CreateEntitlementOverrideBody features(List<String> v) { this.features = v; return this; }
    public CreateEntitlementOverrideBody limits(Map<String, Object> v) { this.limits = v; return this; }
    public CreateEntitlementOverrideBody expiresAt(Long v) { this.expiresAt = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CreateEntitlementOverrideBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
