package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One user's standing on a policy. {@code blocking}: accepting is required before the next sign-in completes. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicyStanding(
        String object,
        String policy,
        String policyId,
        String title,
        String display,
        Boolean required,
        String appliesTo,
        String reaccept,
        String currentVersionId,
        String currentVersion,
        String contentSha256,
        String summaryOfChanges,
        String acceptedVersionId,
        String acceptedVersion,
        Long acceptedAt,
        Boolean needsReaccept,
        Boolean blocking,
        Long graceEndsAt) {
}
