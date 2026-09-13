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
        long updatedAt) {
}
