package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@code POST /v1/platform/apps/{id}/initial_secret/reveal}: a newly created app's secret keys, shown once more
 * within 10 minutes of the create. A second call (or one after the window) is 410; rotate instead.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InitialSecret(
        String object,
        String appId,
        String instanceId,
        String keyId,
        /** The secret key created with the app; null if it was rotated or revoked since. */
        String secret,
        /** The development sibling created with the app; null when there is none. */
        Sibling siblingInstance,
        /** Dry run only: whether the reveal can still be used. */
        Boolean available,
        /** Dry run only: when the window closes (epoch ms). */
        Long expiresAt,
        String note) {

    /** The development sibling's secret key; {@code secret} is null if it was rotated or revoked since. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sibling(String instanceId, String keyId, String secret, String note) {
    }
}
