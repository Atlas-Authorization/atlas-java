package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * A template in the Actions gallery ({@code source} {@code builtin} or {@code community}).
 * The detail view adds {@code description}, {@code code}, {@code changelog},
 * {@code repositoryUrl} and {@code versions}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActionGalleryEntry(
        String object,
        String source,
        String slug,
        String name,
        String summary,
        String trigger,
        String category,
        List<String> tags,
        /** SPDX licence id. */ String license,
        Map<String, Object> author,
        boolean verified,
        boolean featured,
        long installCount,
        String latestVersion,
        /** The code sends outbound requests with api.http. */ boolean usesHttp,
        List<ActionTemplateSecret> secrets,
        Long updatedAt,
        String description,
        String code,
        String changelog,
        String repositoryUrl,
        JsonNode versions) {
}
