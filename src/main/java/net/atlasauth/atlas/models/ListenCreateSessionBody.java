package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/listen/sessions}. */
public final class ListenCreateSessionBody {
    /** Event types or patterns to forward, such as `user.created` or `user.*`. Omit for every event. */
    public List<String> events;
    /** How long the session lives. Default 3600. */
    public Long ttlSeconds;

    public ListenCreateSessionBody() {
    }

    public ListenCreateSessionBody events(List<String> v) { this.events = v; return this; }
    public ListenCreateSessionBody ttlSeconds(Long v) { this.ttlSeconds = v; return this; }
}
