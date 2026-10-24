package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/secret_keys}: a scoped instance secret key. Scopes are instance scopes or the presets {@code read_only}, {@code privacy:execute} and {@code privacy:grants:revoke}. */
public final class CreateSecretKeyBody extends ExtensibleBody {
    public String name;
    public List<String> scopes;
    /** Unix ms, at most one year away. Use {@link #expiresAtIso(String)} for an ISO 8601 string. */
    public Long expiresAt;
    /** CIDRs the key may be used from. */
    public List<String> allowedIps;
    public String owner;
    /** {@code atlas} (default) or {@code legacy} (for validators that check for {@code sk_}). */
    public String keyFormat;

    public CreateSecretKeyBody() {
    }

    public CreateSecretKeyBody name(String v) { this.name = v; return this; }
    public CreateSecretKeyBody scopes(List<String> v) { this.scopes = v; return this; }
    public CreateSecretKeyBody expiresAt(Long v) { this.expiresAt = v; return this; }
    public CreateSecretKeyBody allowedIps(List<String> v) { this.allowedIps = v; return this; }
    public CreateSecretKeyBody owner(String v) { this.owner = v; return this; }
    public CreateSecretKeyBody keyFormat(String v) { this.keyFormat = v; return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public CreateSecretKeyBody set(String key, Object value) { putField(key, value); return this; }

    /** Send {@code expires_at} as an ISO 8601 string instead of epoch milliseconds. */
    public CreateSecretKeyBody expiresAtIso(String iso) { this.expiresAt = null; putField("expires_at", iso); return this; }
}
