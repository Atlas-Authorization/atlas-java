package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateSignInTokenBody;
import net.atlasauth.atlas.models.RevokedSignInToken;
import net.atlasauth.atlas.models.SignInToken;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * The {@code /v1/sign_in_tokens} resource — one-time sign-in tokens.
 *
 * @deprecated {@code POST /v1/sign_in_tokens} is deprecated (Sunset 2026-04-01);
 *     use {@code Sessions.create} ({@code POST /v1/sessions}), which mints a real
 *     redeemable session in one call.
 */
@Deprecated
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

    /** {@code POST /v1/sign_in_tokens/{id}/revoke} — DEPRECATED (Sunset 2027-04-01) — revoke an unused sign-in token before its 60s window elapses. */
    public RevokedSignInToken revoke(String id) {
        return t.post("/v1/sign_in_tokens/" + enc(id) + "/revoke", null, new TypeReference<>() {
        });
    }
}
