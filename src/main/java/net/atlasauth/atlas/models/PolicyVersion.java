package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One version of a policy: draft, published (immutable) or retired. {@code contentSha256} is the hex SHA-256 of {@code contentHtml}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicyVersion(
        String object,
        String id,
        String policyId,
        String policy,
        String version,
        String status,
        String contentMarkdown,
        String contentHtml,
        String contentUrl,
        String contentSha256,
        String summaryOfChanges,
        String locale,
        Long publishedAt,
        Long effectiveAt,
        Long retiredAt,
        Long createdAt,
        Long updatedAt,
        Integer acceptanceCount) {
}
