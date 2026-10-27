package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/iga/campaigns/{id}/items}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaCampaignsItemsResponse(
        String object,
        List<IgaCampaignsItemsResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
