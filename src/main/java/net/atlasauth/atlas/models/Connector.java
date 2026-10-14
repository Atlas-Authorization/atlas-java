package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Connector(
        String object,
        String id,
        String slug,
        String displayName,
        String logoUrl,
        String tagline,
        String description,
        String category,
        List<JsonNode> tags,
        String websiteUrl,
        String docsUrl,
        String supportUrl,
        String setupGuide,
        String protocol,
        String authorizationEndpoint,
        String tokenEndpoint,
        String userinfoEndpoint,
        String issuer,
        List<String> defaultScopes,
        List<JsonNode> apiScopes,
        List<JsonNode> credentialFields,
        List<String> shareModes,
        String sharedClientId,
        boolean sharedRevoked,
        String idpIssuer,
        boolean idpAutoRegister,
        String status,
        boolean verified,
        boolean featured,
        long installCount,
        long viewCount,
        double ratingAvg,
        long ratingCount,
        long createdAt,
        long updatedAt) {
}
