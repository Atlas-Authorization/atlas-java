package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** The Forumulate connection ({@code enabled: false} and nothing else when none is set). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ForumulateIntegration(
        String object,
        boolean enabled,
        String orgId,
        List<ForumulateSpace> spaces,
        String audience,
        String issuer,
        String jwksUrl,
        Integer tokenTtlSeconds,
        /** The external_jwt provider to store on each space. */ Map<String, Object> readerAuthProvider) {
}
