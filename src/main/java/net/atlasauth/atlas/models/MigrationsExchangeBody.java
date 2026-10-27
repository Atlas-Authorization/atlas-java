package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/migrations/{source}/exchange}. Required: token. */
public final class MigrationsExchangeBody {
    /** The old session token (JWT or opaque), as the middleware extracted it. Required. */
    public String token;
    /** false records a browser-session (non-persistent) session. Default true. */
    public Boolean remember;

    public MigrationsExchangeBody() {
    }

    public MigrationsExchangeBody token(String v) { this.token = v; return this; }
    public MigrationsExchangeBody remember(Boolean v) { this.remember = v; return this; }
}
