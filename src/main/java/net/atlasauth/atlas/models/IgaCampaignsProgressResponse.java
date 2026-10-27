package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/iga/campaigns/{id}/progress}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaCampaignsProgressResponse(
        String object,
        String campaignId,
        String status,
        Long total,
        Long pending,
        Long certified,
        Long revoked,
        Long percentComplete,
        Boolean overdue,
        List<IgaCampaignsProgressResponseReviewersItem> reviewers) {
}
