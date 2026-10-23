package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A policy (terms of service, privacy policy, any key) users accept. Timestamps are epoch milliseconds. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Policy(
        String object,
        String id,
        String key,
        String title,
        String display,
        Boolean required,
        String appliesTo,
        String reaccept,
        Integer graceDays,
        String source,
        Integer position,
        String status,
        List<String> overrides,
        PolicyVersionRef currentVersion,
        PolicyVersionRef upcomingVersion,
        PolicyVersionRef latestVersion,
        Long retiredAt,
        Long createdAt,
        Long updatedAt) {
}
