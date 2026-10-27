package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaRequestsListResponseDataItem(
        String object,
        String id,
        String entitlementId,
        String requesterUserId,
        String beneficiaryUserId,
        String source,
        String justification,
        Long durationSeconds,
        String status,
        Long currentStep,
        List<Map<String, Object>> steps,
        List<Map<String, Object>> sod,
        List<Map<String, Object>> approvals,
        String failure,
        Long decidedAt,
        Long createdAt) {
}
