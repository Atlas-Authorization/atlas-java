package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/secret_keys/{id}} and {@code PATCH /v1/platform/keys/{id}}. {@link #clear(String...)} sends {@code name}, {@code owner} or {@code expires_at} as null. */
public final class UpdateSecretKeyBody extends ExtensibleBody {
    public String name;
    public String owner;
    /** Unix ms. */
    public Long expiresAt;
    public List<String> allowedIps;

    public UpdateSecretKeyBody() {
    }

    public UpdateSecretKeyBody name(String v) { this.name = v; return this; }
    public UpdateSecretKeyBody owner(String v) { this.owner = v; return this; }
    public UpdateSecretKeyBody expiresAt(Long v) { this.expiresAt = v; return this; }
    public UpdateSecretKeyBody allowedIps(List<String> v) { this.allowedIps = v; return this; }

    /** Send the named nullable fields (wire names) as {@code null}, clearing the stored value. */
    public UpdateSecretKeyBody clear(String... keys) { putNulls(keys); return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public UpdateSecretKeyBody set(String key, Object value) { putField(key, value); return this; }

}
