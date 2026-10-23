package net.atlasauth.atlas.models;

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

    public UpdateResourceServerBody() {
    }

    public UpdateResourceServerBody name(String v) { this.name = v; return this; }
    public UpdateResourceServerBody scopes(List<Object> v) { this.scopes = v; return this; }
    public UpdateResourceServerBody tokenTtlSeconds(Long v) { this.tokenTtlSeconds = v; return this; }
    public UpdateResourceServerBody signingAlg(String v) { this.signingAlg = v; return this; }
    public UpdateResourceServerBody identifier(String v) { this.identifier = v; return this; }

    public UpdateResourceServerBody publishResourceMetadata(Boolean v) { this.publishResourceMetadata = v; return this; }
}
