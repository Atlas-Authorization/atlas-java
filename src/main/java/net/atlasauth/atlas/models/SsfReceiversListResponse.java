package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/ssf/receivers}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SsfReceiversListResponse(
        String object,
        List<SsfReceiversListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
