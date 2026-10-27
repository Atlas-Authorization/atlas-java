package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/iga/delegations}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaDelegationsListResponse(
        String object,
        List<IgaDelegationsListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
