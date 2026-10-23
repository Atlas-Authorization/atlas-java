package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The external policy source (policy source protocol). The signing secret is write-only; {@code signingSecretSet} says whether one is stored. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicySource(
        String object,
        Boolean enabled,
        String endpointUrl,
        Boolean signingSecretSet,
        String sync,
        Integer pollIntervalMinutes,
        String webhookUrl,
        String protocolVersion,
        Long lastSyncedAt,
        Boolean lastSyncOk,
        String lastSyncError,
        Long nextPollAt) {
}
