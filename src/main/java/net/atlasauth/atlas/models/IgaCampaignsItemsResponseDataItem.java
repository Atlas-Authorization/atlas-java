package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaCampaignsItemsResponseDataItem(
        String object,
        String id,
        String campaignId,
        String grantId,
        String userId,
        String entitlementId,
        String reviewerUserId,
        String decision,
        String decidedBy,
        Long decidedAt,
        String comment,
        Boolean revokedApplied) {
}
