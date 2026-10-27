package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/users/{id}/guardians}. */
public final class UsersGuardiansRequestBody {
    /** The guardian's email address. */
    public String email;
    /** A user of this application who is the guardian. */
    public String guardianUserId;
    /** Free label such as mother or legal guardian. */
    public String relationship;
    /** Email the consent link to the guardian (default false). */
    public Boolean sendEmail;

    public UsersGuardiansRequestBody() {
    }

    public UsersGuardiansRequestBody email(String v) { this.email = v; return this; }
    public UsersGuardiansRequestBody guardianUserId(String v) { this.guardianUserId = v; return this; }
    public UsersGuardiansRequestBody relationship(String v) { this.relationship = v; return this; }
    public UsersGuardiansRequestBody sendEmail(Boolean v) { this.sendEmail = v; return this; }
}
