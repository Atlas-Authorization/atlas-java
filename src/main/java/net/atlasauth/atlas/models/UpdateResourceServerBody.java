package net.atlasauth.atlas.models;

import java.util.Map;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.List;

/**
 * Body for {@code PATCH /v1/resource_servers/:id}. Each entry of {@code scopes}
 * is either a bare {@code String} or a {@link ResourceServerScope}.
 */
public final class UpdateResourceServerBody {
    /** Serve the RFC 9728 protected-resource metadata document for this API. */
    public Boolean publishResourceMetadata;
    public String name;
    /** Each element is a String or a {@link ResourceServerScope}. */
    public List<Object> scopes;
    public Long tokenTtlSeconds;
    public String signingAlg;
    /** Immutable — supplying a different value is rejected. */
    public String identifier;
    /** jwt or opaque; clear it with {@code set("access_token_format", null)}. */
    public String accessTokenFormat;
    /** 300–31536000 seconds. */
    public Long refreshTokenAbsoluteTtlSeconds;
    /** 300–31536000 seconds, at most the absolute lifetime. */
    public Long refreshTokenIdleTtlSeconds;
    /** client_ids of confidential clients that may introspect this API's tokens. */
    public List<String> introspectionClientIds;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateResourceServerBody() {
    }

    public UpdateResourceServerBody name(String v) { this.name = v; return this; }
    public UpdateResourceServerBody scopes(List<Object> v) { this.scopes = v; return this; }
    public UpdateResourceServerBody tokenTtlSeconds(Long v) { this.tokenTtlSeconds = v; return this; }
    public UpdateResourceServerBody signingAlg(String v) { this.signingAlg = v; return this; }
    public UpdateResourceServerBody identifier(String v) { this.identifier = v; return this; }

    public UpdateResourceServerBody publishResourceMetadata(Boolean v) { this.publishResourceMetadata = v; return this; }

    public UpdateResourceServerBody accessTokenFormat(String v) { this.accessTokenFormat = v; return this; }
    public UpdateResourceServerBody refreshTokenAbsoluteTtlSeconds(Long v) { this.refreshTokenAbsoluteTtlSeconds = v; return this; }
    public UpdateResourceServerBody refreshTokenIdleTtlSeconds(Long v) { this.refreshTokenIdleTtlSeconds = v; return this; }
    public UpdateResourceServerBody introspectionClientIds(List<String> v) { this.introspectionClientIds = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public UpdateResourceServerBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
