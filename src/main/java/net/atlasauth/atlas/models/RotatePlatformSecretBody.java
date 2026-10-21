package net.atlasauth.atlas.models;

/** The body of {@code POST /v1/platform/apps/{id}/instances/{instanceId}/rotate_secret}. */
public final class RotatePlatformSecretBody {
    /**
     * How long the prior keys keep working, in seconds. Null uses the default, 259200 (72 hours);
     * 0 revokes them now; at most 604800 (7 days).
     */
    public Integer overlapSeconds;

    public RotatePlatformSecretBody() {
    }

    public RotatePlatformSecretBody overlapSeconds(Integer v) { this.overlapSeconds = v; return this; }
}
