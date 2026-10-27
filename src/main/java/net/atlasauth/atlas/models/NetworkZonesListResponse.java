package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/network_zones}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NetworkZonesListResponse(
        String object,
        List<NetworkZonesListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
