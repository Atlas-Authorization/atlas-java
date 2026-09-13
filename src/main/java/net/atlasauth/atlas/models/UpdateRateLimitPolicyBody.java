package net.atlasauth.atlas.models;

/** Body for {@code PATCH /v1/rate_limit_policy}; omitted knobs are left unchanged. */
public final class UpdateRateLimitPolicyBody {
    public Integer fapiCreatePerMin;
    public Integer bapiPerMin;

    public UpdateRateLimitPolicyBody fapiCreatePerMin(Integer v) { this.fapiCreatePerMin = v; return this; }
    public UpdateRateLimitPolicyBody bapiPerMin(Integer v) { this.bapiPerMin = v; return this; }
}
