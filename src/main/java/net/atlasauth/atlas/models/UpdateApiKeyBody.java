package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code PATCH /v1/api_keys/:id}. */
public final class UpdateApiKeyBody {
    public String name;
    public Map<String, Object> claims;
    public Long expiresAt;

    public UpdateApiKeyBody() {
    }

    public UpdateApiKeyBody name(String v) { this.name = v; return this; }
    public UpdateApiKeyBody claims(Map<String, Object> v) { this.claims = v; return this; }
    public UpdateApiKeyBody expiresAt(Long v) { this.expiresAt = v; return this; }
}
