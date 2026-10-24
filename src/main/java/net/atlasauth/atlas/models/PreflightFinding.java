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
        String suppressedReason) {
    /** The shape before {@code group}, {@code suppressed}, {@code suppressedReason}. */
    public PreflightFinding(String severity, String code, String message, String fix, Map<String, String> subject) {
        this(severity, code, message, fix, subject, null, null, null);
    }

}
