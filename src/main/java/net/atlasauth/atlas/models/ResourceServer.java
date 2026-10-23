package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A resource server (API) that issues access tokens with named scopes. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResourceServer(
        String object,
        String id,
        String identifier,
        String name,
        List<ResourceServerScope> scopes,
        long tokenTtlSeconds,
        String signingAlg,
        long createdAt,
        long updatedAt,
        /** Atlas serves the RFC 9728 protected-resource metadata document for this API. */ boolean publishResourceMetadata,
        /** jwt or opaque access tokens for this API; null inherits. */ String accessTokenFormat,
        /** Refresh-token absolute lifetime for grants issued for this API (seconds); null inherits. */ Long refreshTokenAbsoluteTtlSeconds,
        /** Refresh-token idle lifetime (seconds); null inherits. */ Long refreshTokenIdleTtlSeconds,
        /** client_ids of the confidential clients allowed to introspect this API's tokens. */ List<String> introspectionClientIds) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public ResourceServer(String object, String id, String identifier, String name, List<ResourceServerScope> scopes, long tokenTtlSeconds, String signingAlg, long createdAt, long updatedAt, boolean publishResourceMetadata) {
        this(object, id, identifier, name, scopes, tokenTtlSeconds, signingAlg, createdAt, updatedAt, publishResourceMetadata, null, null, null, null);
    }

    /** The 1.10 shape, before the 1.11 fields. */
    public ResourceServer(String object, String id, String identifier, String name, List<ResourceServerScope> scopes, long tokenTtlSeconds, String signingAlg, long createdAt, long updatedAt) {
        this(object, id, identifier, name, scopes, tokenTtlSeconds, signingAlg, createdAt, updatedAt, false);
    }
}
