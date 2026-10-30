package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/api_keys}. */
public final class CreateApiKeyBody {
    /** Alternative to expiresAt; without either the instance default lifetime applies. */
    public Long expiresInSeconds;
    /** A custom secret prefix, e.g. "acme" gives acme_<id>_<secret>. */
    public String prefix;
    /** False mints the key disabled. */
    public Boolean enabled;
    /** A per-key verify throttle. */
    public ApiKeyRateLimit rateLimit;
    /** A use budget each successful verify spends. */
    public Long remaining;
    /** Scopes the key carries (checked by verify requiredScopes). */
    public java.util.List<String> scopes;
    /** "user" or "organization". */
    public String subjectType;
    public String subjectId;
    public String name;
    public Map<String, Object> claims;
    /** Epoch ms; null (or omitted) never expires. */
    public Long expiresAt;
    /** Per-key spend caps, metered with {@code ApiKeys.recordUsage}. */
    public List<ApiKeyLimit> limits;
    /** Bring over a key minted elsewhere by its hash: no secret is returned, and the original secret verifies. */
    @JsonProperty("import")
    public ApiKeyImport importKey;

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
    public CreateApiKeyBody limits(List<ApiKeyLimit> v) { this.limits = v; return this; }
    public CreateApiKeyBody importKey(ApiKeyImport v) { this.importKey = v; return this; }

    public CreateApiKeyBody expiresInSeconds(Long v) { this.expiresInSeconds = v; return this; }
    public CreateApiKeyBody prefix(String v) { this.prefix = v; return this; }
    public CreateApiKeyBody enabled(Boolean v) { this.enabled = v; return this; }
    public CreateApiKeyBody rateLimit(ApiKeyRateLimit v) { this.rateLimit = v; return this; }
    public CreateApiKeyBody remaining(Long v) { this.remaining = v; return this; }
    public CreateApiKeyBody scopes(java.util.List<String> v) { this.scopes = v; return this; }
}
