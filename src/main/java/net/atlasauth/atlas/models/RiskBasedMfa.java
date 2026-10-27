package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Adaptive / risk-based MFA config. {@code stepUpThreshold} is {@code "low"},
 * {@code "medium"}, or {@code "high"}; {@code onHighRiskNoFactor} is
 * {@code "allow"}, {@code "block"}, or {@code "require_enroll"}. {@code mode} is
 * {@code "enforce"} or {@code "audit"}; {@code levelActions}, {@code reputation},
 * {@code fraud}, {@code baseline} and {@code midSession} are the raw config objects.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RiskBasedMfa(
        String object,
        boolean enabled,
        String stepUpThreshold,
        String onHighRiskNoFactor,
        RiskBasedMfaWeights weights,
        RiskBasedMfaSignals signals,
        String mode,
        Map<String, Object> levelActions,
        Map<String, Object> reputation,
        Map<String, Object> fraud,
        Map<String, Object> baseline,
        Map<String, Object> midSession) {

    /** The shape before the audit mode, fraud rules, reputation, baseline and mid-session fields. */
    public RiskBasedMfa(String object, boolean enabled, String stepUpThreshold, String onHighRiskNoFactor,
                        RiskBasedMfaWeights weights, RiskBasedMfaSignals signals) {
        this(object, enabled, stepUpThreshold, onHighRiskNoFactor, weights, signals, null, null, null, null, null, null);
    }
}
