package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ActorToken;
import net.atlasauth.atlas.models.CreateActorTokenBody;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/actor_tokens} resource — impersonation tokens. */
public final class ActorTokens {

    private final Transport t;

    public ActorTokens(Transport t) {
        this.t = t;
    }

    public ActorToken create(CreateActorTokenBody body) {
        return create(body, null);
    }

    public ActorToken create(CreateActorTokenBody body, String idempotencyKey) {
        return t.post("/v1/actor_tokens", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** Revoke an actor token before it is used. Returns {@code {object, id, revoked}}. */
    public JsonNode revoke(String id) {
        return t.post("/v1/actor_tokens/" + enc(id) + "/revoke", null, new TypeReference<>() {
        });
    }
}
