package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/iga/campaigns/{id}/evidence}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaCampaignsEvidenceResponse(
        String object,
        IgaCampaignsListResponseDataItem campaign,
        IgaCampaignsProgressResponse progress,
        String generatedAt,
        List<IgaCampaignsEvidenceResponseItemsItem> items) {
}
