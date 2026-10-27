package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/users/{id}/notes}. Required: body. */
public final class UsersNotesCreateBody {
    /** The note text. Required. */
    public String body;
    /** Pinned notes are listed first. */
    public Boolean pinned;

    public UsersNotesCreateBody() {
    }

    public UsersNotesCreateBody body(String v) { this.body = v; return this; }
    public UsersNotesCreateBody pinned(Boolean v) { this.pinned = v; return this; }
}
