package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A template this instance publishes ({@code draft}, {@code in_review}, {@code published}, {@code unlisted}, {@code rejected} or {@code banned}). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActionTemplate(
        String object,
        String id,
        String slug,
        String name,
        String authorName,
        String summary,
        String description,
        String category,
        List<String> tags,
        String trigger,
        String license,
        String repositoryUrl,
        String status,
        String latestVersion,
        boolean verified,
        boolean featured,
        long installCount,
        long viewCount,
        Long publishedAt,
        long createdAt,
        long updatedAt,
        List<ActionTemplateVersion> versions,
        boolean deleted) {
}
