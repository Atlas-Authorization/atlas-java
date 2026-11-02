package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A stored preflight acknowledgement ({@code /v1/instance/preflight/acks}). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PreflightAck(
        /** {@code preflight_ack}. */ String object,
        /** The preflight code it acknowledges (every finding with this code). */ String code,
        String reason,
        /** The API key or dashboard user that acknowledged it. */ String by,
        /** Epoch ms. */ long at,
        /** Epoch ms; null = until removed. */ Long expiresAt,
        /** False once {@code expiresAt} has passed: the finding counts again. */ boolean active) {
}
