package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PreflightFinding(
        String severity,
        String code,
        String message,
        String fix,
        Map<String, String> subject,
        /** {@code security_posture} on findings about settings weaker than the defaults new instances get. */ String group,
        /** Set only with {@code include_suppressed}: the finding does not apply to how the instance integrates. */ Boolean suppressed,
        String suppressedReason,
        /** Set when the code is acknowledged as known and accepted (never on an error): do not count the finding. */ PreflightAckSummary acknowledged) {
    /** The 1.20 shape, before {@code acknowledged}. */
    public PreflightFinding(String severity, String code, String message, String fix, Map<String, String> subject, String group, Boolean suppressed, String suppressedReason) {
        this(severity, code, message, fix, subject, group, suppressed, suppressedReason, null);
    }

    /** The shape before {@code group}, {@code suppressed}, {@code suppressedReason}. */
    public PreflightFinding(String severity, String code, String message, String fix, Map<String, String> subject) {
        this(severity, code, message, fix, subject, null, null, null, null);
    }

    /** A finding's {@code acknowledged}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PreflightAckSummary(
            String reason,
            /** The API key or dashboard user that acknowledged it. */ String by,
            /** Epoch ms. */ long at,
            /** Epoch ms; null = until removed. */ Long expiresAt) {
    }
}
