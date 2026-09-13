package net.atlasauth.atlas.models;

/**
 * Body for {@code PATCH /v1/risk_based_mfa}; omitted fields are left unchanged.
 * {@code stepUpThreshold} is {@code "low"}/{@code "medium"}/{@code "high"};
 * {@code onHighRiskNoFactor} is {@code "allow"}/{@code "block"}/{@code "require_enroll"}.
 */
public final class UpdateRiskBasedMfaBody {
    public Boolean enabled;
    public String stepUpThreshold;
    public String onHighRiskNoFactor;
    public RiskBasedMfaWeightsBody weights;
    public RiskBasedMfaSignalsBody signals;

    public UpdateRiskBasedMfaBody enabled(Boolean v) { this.enabled = v; return this; }
    public UpdateRiskBasedMfaBody stepUpThreshold(String v) { this.stepUpThreshold = v; return this; }
    public UpdateRiskBasedMfaBody onHighRiskNoFactor(String v) { this.onHighRiskNoFactor = v; return this; }
    public UpdateRiskBasedMfaBody weights(RiskBasedMfaWeightsBody v) { this.weights = v; return this; }
    public UpdateRiskBasedMfaBody signals(RiskBasedMfaSignalsBody v) { this.signals = v; return this; }
}
