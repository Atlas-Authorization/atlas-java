package net.atlasauth.atlas.models;

import java.util.Map;

/**
 * Body for {@code PATCH /v1/risk_based_mfa}; omitted fields are left unchanged.
 * {@code stepUpThreshold} is {@code "low"}/{@code "medium"}/{@code "high"};
 * {@code onHighRiskNoFactor} is {@code "allow"}/{@code "block"}/{@code "require_enroll"}.
 * {@code mode} is {@code "enforce"}/{@code "audit"}; {@code levelActions} maps
 * {@code medium}/{@code high} to {@code default}/{@code allow}/{@code notify}/{@code mfa}/{@code block};
 * {@code reputation}, {@code fraud}, {@code baseline} and {@code midSession} are partial config objects.
 */
public final class UpdateRiskBasedMfaBody {
    public Boolean enabled;
    public String stepUpThreshold;
    public String onHighRiskNoFactor;
    public RiskBasedMfaWeightsBody weights;
    public RiskBasedMfaSignalsBody signals;
    public String mode;
    public Map<String, Object> levelActions;
    public Map<String, Object> reputation;
    public Map<String, Object> fraud;
    public Map<String, Object> baseline;
    public Map<String, Object> midSession;

    public UpdateRiskBasedMfaBody enabled(Boolean v) { this.enabled = v; return this; }
    public UpdateRiskBasedMfaBody stepUpThreshold(String v) { this.stepUpThreshold = v; return this; }
    public UpdateRiskBasedMfaBody onHighRiskNoFactor(String v) { this.onHighRiskNoFactor = v; return this; }
    public UpdateRiskBasedMfaBody weights(RiskBasedMfaWeightsBody v) { this.weights = v; return this; }
    public UpdateRiskBasedMfaBody signals(RiskBasedMfaSignalsBody v) { this.signals = v; return this; }
    public UpdateRiskBasedMfaBody mode(String v) { this.mode = v; return this; }
    public UpdateRiskBasedMfaBody levelActions(Map<String, Object> v) { this.levelActions = v; return this; }
    public UpdateRiskBasedMfaBody reputation(Map<String, Object> v) { this.reputation = v; return this; }
    public UpdateRiskBasedMfaBody fraud(Map<String, Object> v) { this.fraud = v; return this; }
    public UpdateRiskBasedMfaBody baseline(Map<String, Object> v) { this.baseline = v; return this; }
    public UpdateRiskBasedMfaBody midSession(Map<String, Object> v) { this.midSession = v; return this; }
}
