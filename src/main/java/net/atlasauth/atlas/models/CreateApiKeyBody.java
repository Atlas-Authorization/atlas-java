package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code POST /v1/api_keys}. */
public final class CreateApiKeyBody {
    /** "user" or "organization". */
    public String subjectType;
    public String subjectId;
    public String name;
    public Map<String, Object> claims;
    /** Epoch ms; null (or omitted) never expires. */
    public Long expiresAt;

    public CreateApiKeyBody() {
    }

    public CreateApiKeyBody(String subjectType, String subjectId) {
        this.subjectType = subjectType;
        this.subjectId = subjectId;
    }

    public CreateApiKeyBody subjectType(String v) { this.subjectType = v; return this; }
    public CreateApiKeyBody subjectId(String v) { this.subjectId = v; return this; }
    public CreateApiKeyBody name(String v) { this.name = v; return this; }
    public CreateApiKeyBody claims(Map<String, Object> v) { this.claims = v; return this; }
    public CreateApiKeyBody expiresAt(Long v) { this.expiresAt = v; return this; }
}
