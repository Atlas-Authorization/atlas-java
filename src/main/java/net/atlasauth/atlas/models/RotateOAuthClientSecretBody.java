package net.atlasauth.atlas.models;

/** The body of {@code POST /v1/oauth_clients/{id}/rotate_secret}. */
public final class RotateOAuthClientSecretBody {
    /**
     * How long the previous secret keeps working, in seconds. Null uses the default, 86400 (24
     * hours); 0 revokes it now (after a leak); at most 604800 (7 days).
     */
    public Integer overlapSeconds;

    public RotateOAuthClientSecretBody() {
    }

    public RotateOAuthClientSecretBody overlapSeconds(Integer v) { this.overlapSeconds = v; return this; }
}
