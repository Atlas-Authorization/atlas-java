package net.atlasauth.atlas.models;

/** The body of the Sign in with Atlas app rotate calls (client secret and webhook secret). */
public final class RotateAtlasIdAppSecretBody {
    /**
     * How long the previous secret keeps working, in seconds; at most 604800 (7 days). Null uses
     * the default: 86400 (24 hours) for the client secret, 0 (it stops at once) for the webhook
     * secret. 0 stops it now.
     */
    public Integer overlapSeconds;

    public RotateAtlasIdAppSecretBody() {
    }

    public RotateAtlasIdAppSecretBody overlapSeconds(Integer v) { this.overlapSeconds = v; return this; }
}
