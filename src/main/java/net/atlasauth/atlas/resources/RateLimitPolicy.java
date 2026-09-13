package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.UpdateRateLimitPolicyBody;

/**
 * The {@code /v1/rate_limit_policy} resource. The model this returns shares its
 * simple name, so it is referenced fully-qualified.
 */
public final class RateLimitPolicy {

    private final Transport t;

    public RateLimitPolicy(Transport t) {
        this.t = t;
    }

    /** Read the current policy and the bounds each knob is clamped to. */
    public net.atlasauth.atlas.models.RateLimitPolicy get() {
        return t.get("/v1/rate_limit_policy", new TypeReference<>() {
        });
    }

    /** Adjust either budget; omitted knobs are left unchanged. */
    public net.atlasauth.atlas.models.RateLimitPolicy update(UpdateRateLimitPolicyBody body) {
        return t.patch("/v1/rate_limit_policy", body, new TypeReference<>() {
        });
    }
}
