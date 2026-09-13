package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateSignInTokenBody;
import net.atlasauth.atlas.models.SignInToken;

/** The {@code /v1/sign_in_tokens} resource — one-time sign-in tokens. */
public final class SignInTokens {

    private final Transport t;

    public SignInTokens(Transport t) {
        this.t = t;
    }

    public SignInToken create(CreateSignInTokenBody body) {
        return create(body, null);
    }

    public SignInToken create(CreateSignInTokenBody body, String idempotencyKey) {
        return t.post("/v1/sign_in_tokens", body, idempotencyKey, new TypeReference<>() {
        });
    }
}
