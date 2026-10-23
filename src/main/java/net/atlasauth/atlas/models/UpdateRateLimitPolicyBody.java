package net.atlasauth.atlas.models;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
/** Body for {@code PATCH /v1/rate_limit_policy}; omitted knobs are left unchanged. */
public final class UpdateRateLimitPolicyBody {
    public Integer fapiCreatePerMin;
    public Integer bapiPerMin;
    public Integer fapiVerifyPerMin;
    public Integer ldapPerIpPerMin;
    public Integer ldapPerUserPerMin;
    /** The FULL rule list (replaces the stored one; an empty list clears it). */
    public List<RateLimitRule> rules;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateRateLimitPolicyBody fapiCreatePerMin(Integer v) { this.fapiCreatePerMin = v; return this; }
    public UpdateRateLimitPolicyBody bapiPerMin(Integer v) { this.bapiPerMin = v; return this; }

    public UpdateRateLimitPolicyBody fapiVerifyPerMin(Integer v) { this.fapiVerifyPerMin = v; return this; }
    public UpdateRateLimitPolicyBody ldapPerIpPerMin(Integer v) { this.ldapPerIpPerMin = v; return this; }
    public UpdateRateLimitPolicyBody ldapPerUserPerMin(Integer v) { this.ldapPerUserPerMin = v; return this; }
    public UpdateRateLimitPolicyBody rules(List<RateLimitRule> v) { this.rules = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public UpdateRateLimitPolicyBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
