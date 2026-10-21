package net.atlasauth.atlas.models;

/** The body of {@code POST /v1/webhook_endpoints/{id}/rotate_secret}. */
public final class RotateWebhookSecretBody {
    /**
     * How long the previous secret keeps signing deliveries (as a second {@code atlas-signature}
     * entry), in seconds. Null or 0: it stops at once (the default); at most 604800 (7 days). Set
     * it only once every receiver verifies with a verifier that accepts several signatures (1.5.0+).
     */
    public Integer overlapSeconds;

    public RotateWebhookSecretBody() {
    }

    public RotateWebhookSecretBody overlapSeconds(Integer v) { this.overlapSeconds = v; return this; }
}
