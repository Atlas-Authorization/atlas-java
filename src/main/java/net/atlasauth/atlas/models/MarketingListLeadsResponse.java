package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/leads}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MarketingListLeadsResponse(
        String object,
        List<MarketingListLeadsResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
