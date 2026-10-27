package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProxyAppsListResponseDataItem(
        String object,
        String id,
        String name,
        String hostname,
        String upstreamUrl,
        List<Map<String, Object>> rules,
        List<String> publicPaths,
        Boolean injectHeaders,
        Boolean injectToken,
        Boolean allowAgentTokens,
        Boolean enabled,
        Long createdAt,
        Long updatedAt) {
}
