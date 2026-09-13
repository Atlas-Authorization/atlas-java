package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.TokenVerification;
import net.atlasauth.atlas.models.VerifyTokenBody;

/** The {@code /v1/tokens} resource — authoritative session-token verification. */
public final class Tokens {

    private final Transport t;

    public Tokens(Transport t) {
        this.t = t;
    }

    /**
     * Authoritatively verify a session JWT, checking the revocation set (and the
     * database when the cache cannot answer). A failure is reported as
     * {@code verified: false} with a coarse {@code reason}.
     */
    public TokenVerification verify(VerifyTokenBody body) {
        return t.post("/v1/tokens/verify", body, new TypeReference<>() {
        });
    }
}
