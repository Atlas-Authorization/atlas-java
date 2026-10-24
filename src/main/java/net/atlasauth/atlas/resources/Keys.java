package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.StandardKey;
import net.atlasauth.atlas.models.Whoami;

/** {@code /v1/keys} and {@code /v1/whoami} — every credential on the instance in one shape, and what the key in use may do. */
public final class Keys {
    private final Transport t;

    public Keys(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/keys} — Every credential on the instance (secret keys, webhook secrets, partner links, access tokens) in the standard shape. */
    public ListPage<StandardKey> list() {
        return t.get("/v1/keys", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/whoami} — What the credential in use is, its scopes and the instance it belongs to. */
    public Whoami whoami() {
        return t.get("/v1/whoami", new TypeReference<>() {
        });
    }
}
