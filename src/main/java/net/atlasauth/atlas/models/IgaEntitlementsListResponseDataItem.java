package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaEntitlementsListResponseDataItem(
        String object,
        String id,
        String key,
        String name,
        String description,
        String kind,
        Map<String, Object> target,
        String ownerUserId,
        String riskLevel,
        Map<String, Object> approvalRules,
        Long maxDurationSeconds,
        Boolean requestable,
        Boolean requireJustification,
        Long createdAt,
        Long updatedAt) {
}
