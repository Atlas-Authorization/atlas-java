package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/api_keys/:id}. */
public final class UpdateApiKeyBody {
    public String name;
    public Map<String, Object> claims;
    public Long expiresAt;
    /** Replace the key's spend caps. Use {@link #clearLimits()} to remove them all. */
    public List<ApiKeyLimit> limits;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateApiKeyBody() {
    }

    public UpdateApiKeyBody name(String v) { this.name = v; return this; }
    public UpdateApiKeyBody claims(Map<String, Object> v) { this.claims = v; return this; }
    public UpdateApiKeyBody expiresAt(Long v) { this.expiresAt = v; return this; }
    public UpdateApiKeyBody limits(List<ApiKeyLimit> v) { this.limits = v; return this; }

    /** Remove every spend cap (sends {@code "limits": null}). */
    public UpdateApiKeyBody clearLimits() { this.limits = null; return set("limits", null); }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public UpdateApiKeyBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
