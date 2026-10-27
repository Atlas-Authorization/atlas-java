package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaSodViolationsResponseDataItem(
        String object,
        String id,
        String ruleId,
        String ruleName,
        String mode,
        String userId,
        String status,
        String source,
        String requestId,
        String note,
        Long detectedAt,
        Long resolvedAt) {
}
