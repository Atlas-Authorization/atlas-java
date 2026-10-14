package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class SetUserPasswordBody {
    /** The new plaintext password; Argon2id-hashed server-side. */
    public String password;
    /** Clerk-parity flag; a no-op here since an admin set runs no policy/breach check. */
    public Boolean skipPasswordChecks;
    /** Revoke the user's live sessions and bump sessions_version (JWTs die within 60s). */
    public Boolean signOutOfOtherSessions;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public SetUserPasswordBody() {
    }

    public SetUserPasswordBody password(String v) { this.password = v; return this; }
    public SetUserPasswordBody skipPasswordChecks(Boolean v) { this.skipPasswordChecks = v; return this; }
    public SetUserPasswordBody signOutOfOtherSessions(Boolean v) { this.signOutOfOtherSessions = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public SetUserPasswordBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
