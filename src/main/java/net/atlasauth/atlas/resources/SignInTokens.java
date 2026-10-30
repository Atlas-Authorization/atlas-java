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
 * @deprecated The sign-in token API is deprecated (sunset 2027-04-01). Use
 *     {@link Sessions#createBrowserTicket(String, String, Long)}: the same one-time ticket, plus a ready URL.
 */
@Deprecated
public final class SignInTokens {

    private final Transport t;

    public SignInTokens(Transport t) {
        this.t = t;
    }

    /** @deprecated Use {@link Sessions#createBrowserTicket(String, String, Long)}. */
    @Deprecated
    public SignInToken create(CreateSignInTokenBody body) {
        return create(body, null);
    }

    /** @deprecated Use {@link Sessions#createBrowserTicket(String, String, Long)}. */
    @Deprecated
    public SignInToken create(CreateSignInTokenBody body, String idempotencyKey) {
        return t.post("/v1/sign_in_tokens", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /**
     * {@code POST /v1/sign_in_tokens/{id}/revoke} - revoke an unused sign-in token before its 60s window elapses.
     *
     * @deprecated Sunset 2027-04-01. Use {@link Sessions#createBrowserTicket(String, String, Long)}.
     */
    @Deprecated
    public RevokedSignInToken revoke(String id) {
        return t.post("/v1/sign_in_tokens/" + enc(id) + "/revoke", null, new TypeReference<>() {
        });
    }
}
