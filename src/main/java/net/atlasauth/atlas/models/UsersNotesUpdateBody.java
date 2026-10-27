package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/users/{id}/notes/{noteId}}. */
public final class UsersNotesUpdateBody {
    /** The note text. */
    public String body;
    /** Pinned notes are listed first. */
    public Boolean pinned;

    public UsersNotesUpdateBody() {
    }

    public UsersNotesUpdateBody body(String v) { this.body = v; return this; }
    public UsersNotesUpdateBody pinned(Boolean v) { this.pinned = v; return this; }
}
