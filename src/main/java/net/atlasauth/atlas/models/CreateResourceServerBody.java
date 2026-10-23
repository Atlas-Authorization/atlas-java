package net.atlasauth.atlas.models;

import java.util.List;

/**
 * Body for {@code POST /v1/resource_servers}. Each entry of {@code scopes} is
 * either a bare {@code String} or a {@link ResourceServerScope}.
 */
public final class CreateResourceServerBody {
    /** Serve the RFC 9728 protected-resource metadata document for this API. */
    public Boolean publishResourceMetadata;
    public String identifier;
    public String name;
    /** Each element is a String or a {@link ResourceServerScope}. */
    public List<Object> scopes;
    public Long tokenTtlSeconds;
    public String signingAlg;

    public CreateResourceServerBody() {
    }

    public CreateResourceServerBody(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public CreateResourceServerBody identifier(String v) { this.identifier = v; return this; }
    public CreateResourceServerBody name(String v) { this.name = v; return this; }
    public CreateResourceServerBody scopes(List<Object> v) { this.scopes = v; return this; }
    public CreateResourceServerBody tokenTtlSeconds(Long v) { this.tokenTtlSeconds = v; return this; }
    public CreateResourceServerBody signingAlg(String v) { this.signingAlg = v; return this; }

    public CreateResourceServerBody publishResourceMetadata(Boolean v) { this.publishResourceMetadata = v; return this; }
}
