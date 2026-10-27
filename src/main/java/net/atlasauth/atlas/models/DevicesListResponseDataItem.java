package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DevicesListResponseDataItem(
        String object,
        String id,
        String userId,
        String source,
        String externalId,
        String name,
        String platform,
        Boolean managed,
        Boolean compliant,
        Map<String, Object> posture,
        Long lastReportedAt,
        Long createdAt) {
}
