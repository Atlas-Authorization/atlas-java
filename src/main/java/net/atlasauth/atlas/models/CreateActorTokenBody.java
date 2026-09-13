package net.atlasauth.atlas.models;

/**
 * Body for {@code POST /v1/actor_tokens}. {@code userId} and {@code actor} are
 * required; {@code expiresInSeconds} is clamped server-side to [1, 3600] and
 * defaults to 60. Fluent setters return {@code this}.
 */
public final class CreateActorTokenBody {
    public String userId;
    public ActorTokenActor actor;
    public Integer expiresInSeconds;

    public CreateActorTokenBody() {
    }

    public CreateActorTokenBody(String userId, ActorTokenActor actor) {
        this.userId = userId;
        this.actor = actor;
    }

    public CreateActorTokenBody userId(String v) { this.userId = v; return this; }
    public CreateActorTokenBody actor(ActorTokenActor v) { this.actor = v; return this; }
    public CreateActorTokenBody expiresInSeconds(Integer v) { this.expiresInSeconds = v; return this; }
}
