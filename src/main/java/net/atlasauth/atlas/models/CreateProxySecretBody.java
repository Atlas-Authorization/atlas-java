package net.atlasauth.atlas.models;

/** The body of {@code POST /v1/instance/proxy_secret}. */
public final class CreateProxySecretBody {
    /**
     * On a rotation: how long the replaced secret keeps working, in seconds. Null uses the
     * default, 86400 (24 hours); 0 stops it now; at most 604800 (7 days).
     */
    public Integer overlapSeconds;

    public CreateProxySecretBody() {
    }

    public CreateProxySecretBody overlapSeconds(Integer v) { this.overlapSeconds = v; return this; }
}
