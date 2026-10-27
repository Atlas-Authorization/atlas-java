package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/iga/requests}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaRequestsListResponse(
        String object,
        List<IgaRequestsListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
