package net.atlasauth.atlas.models;

/**
 * Body for {@code POST /v1/sign_in_tokens}. Only {@code userId} is required;
 * {@code expiresInSeconds} is accepted by the wire type but currently ignored
 * server-side (the token always lives 60 seconds). Fluent setters return
 * {@code this}.
 */
public final class CreateSignInTokenBody {
    public String userId;
    public Integer expiresInSeconds;

    public CreateSignInTokenBody() {
    }

    public CreateSignInTokenBody(String userId) {
        this.userId = userId;
    }

    public CreateSignInTokenBody userId(String v) { this.userId = v; return this; }
    public CreateSignInTokenBody expiresInSeconds(Integer v) { this.expiresInSeconds = v; return this; }
}
