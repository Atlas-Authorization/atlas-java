package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NetworkZonesListResponseDataItem(
        String object,
        String id,
        String name,
        String description,
        List<String> cidrs,
        List<String> countries,
        List<Long> asns,
        List<String> anonymizers,
        Boolean enabled,
        Long createdAt,
        Long updatedAt) {
}
