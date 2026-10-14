package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** A connector catalog entry, emitted verbatim from core with camelCase keys. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DirectoryEntry(
        String source,
        String key,
        String slug,
        @JsonProperty("displayName") String displayName,
        String category,
        String tagline,
        String description,
        @JsonProperty("logoUrl") String logoUrl,
        @JsonProperty("websiteUrl") String websiteUrl,
        @JsonProperty("docsUrl") String docsUrl,
        Boolean verified,
        Boolean featured,
        @JsonProperty("installCount") Long installCount,
        @JsonProperty("ratingAvg") Double ratingAvg,
        @JsonProperty("ratingCount") Long ratingCount,
        @JsonProperty("rankScore") Double rankScore,
        @JsonProperty("defaultScopes") List<String> defaultScopes,
        @JsonProperty("apiScopes") List<JsonNode> apiScopes,
        @JsonProperty("credentialFields") List<JsonNode> credentialFields,
        @JsonProperty("shareModes") List<String> shareModes) {
}
