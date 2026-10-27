package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/users/{id}/guardians/{linkId}/approve}. */
public final class UsersGuardiansApproveBody {
    /** How consent was obtained (kept on the link). */
    public String note;

    public UsersGuardiansApproveBody() {
    }

    public UsersGuardiansApproveBody note(String v) { this.note = v; return this; }
}
