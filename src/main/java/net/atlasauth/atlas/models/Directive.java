package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Directive(
        String object,
        String userId,
        String suspendedUntil,
        Boolean readOnly,
        Boolean forcedLogout,
        String reason,
        long sessionsRevoked) {
}
