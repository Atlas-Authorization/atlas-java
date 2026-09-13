package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Adaptive / risk-based MFA config. {@code stepUpThreshold} is {@code "low"},
 * {@code "medium"}, or {@code "high"}; {@code onHighRiskNoFactor} is
 * {@code "allow"}, {@code "block"}, or {@code "require_enroll"}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RiskBasedMfa(
        String object,
        boolean enabled,
        String stepUpThreshold,
        String onHighRiskNoFactor,
        RiskBasedMfaWeights weights,
        RiskBasedMfaSignals signals) {
}
