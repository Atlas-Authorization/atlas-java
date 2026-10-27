package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaCampaignsListResponseDataItem(
        String object,
        String id,
        String name,
        String description,
        String scopeType,
        List<String> scopeIds,
        String reviewerType,
        List<String> reviewerIds,
        String status,
        Long dueAt,
        List<Long> reminderDays,
        Boolean autoRevoke,
        Long launchedAt,
        Long closedAt,
        Long createdAt) {
}
