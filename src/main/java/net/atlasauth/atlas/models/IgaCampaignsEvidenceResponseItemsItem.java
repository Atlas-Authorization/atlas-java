package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaCampaignsEvidenceResponseItemsItem(
        String itemId,
        String userId,
        Boolean userErased,
        String userEmailMasked,
        String entitlementKey,
        String entitlementName,
        String riskLevel,
        String reviewerUserId,
        String decision,
        String decidedBy,
        String decidedAt,
        Boolean revokedApplied) {
}
