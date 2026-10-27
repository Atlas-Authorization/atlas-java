package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/iga/sod_violations}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaSodViolationsResponse(
        String object,
        List<IgaSodViolationsResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
