package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * A data-rights request on the executor protocol. The wire names are camelCase. {@code type} is the
 * canonical type (access, deletion, closure, restriction, rectification); {@code status} is pending,
 * scheduled, processing, completed, completed_with_retention, rejected, no_match or cancelled.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PrivacyRequest(
        String object,
        String id,
        String type,
        @JsonProperty("canonicalType") String canonicalType,
        @JsonProperty("requestedType") String requestedType,
        String status,
        @JsonProperty("subjectId") String subjectId,
        @JsonProperty("caseId") String caseId,
        @JsonProperty("executeAfter") String executeAfter,
        String eta,
        String detail,
        /** Categories kept after an erasure, with the legal basis and retention end. */ List<JsonNode> retention,
        /** The steps of carrying out the request. */ List<JsonNode> steps,
        String trace,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("updatedAt") String updatedAt,
        @JsonProperty("completedAt") String completedAt,
        /** Set on a finished request (GET). */ JsonNode result) {
}
