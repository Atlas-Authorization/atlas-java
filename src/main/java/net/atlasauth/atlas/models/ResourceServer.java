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
        /** Atlas serves the RFC 9728 protected-resource metadata document for this API. */ boolean publishResourceMetadata) {

    /** The 1.10 shape, before the 1.11 fields. */
    public ResourceServer(String object, String id, String identifier, String name, List<ResourceServerScope> scopes, long tokenTtlSeconds, String signingAlg, long createdAt, long updatedAt) {
        this(object, id, identifier, name, scopes, tokenTtlSeconds, signingAlg, createdAt, updatedAt, false);
    }
}
