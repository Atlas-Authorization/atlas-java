package net.atlasauth.atlas.models;

/**
 * Body for {@code PATCH /v1/instance/sign_in_with_atlas}. {@code enabled(true)} links an existing
 * connection to the Atlas issuer (or the one named by {@code connectionId}), else creates one.
 */
public final class UpdateSignInWithAtlasBody extends ExtensibleBody {
    public Boolean enabled;
    /** Link this existing connection (an active OIDC connection to the Atlas issuer). Omit to pick or create one. */
    public String connectionId;

    public UpdateSignInWithAtlasBody() {
    }

    public UpdateSignInWithAtlasBody(boolean enabled) {
        this.enabled = enabled;
    }

    public UpdateSignInWithAtlasBody enabled(Boolean v) { this.enabled = v; return this; }
    public UpdateSignInWithAtlasBody connectionId(String v) { this.connectionId = v; return this; }
    public UpdateSignInWithAtlasBody set(String key, Object value) { putField(key, value); return this; }
}
