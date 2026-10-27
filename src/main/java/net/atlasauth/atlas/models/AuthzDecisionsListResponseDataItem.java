package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthzDecisionsListResponseDataItem(
        String object,
        String id,
        String source,
        String decision,
        String principal,
        String action,
        String resource,
        Map<String, Object> input,
        Map<String, Object> result,
        String policyRevision,
        Map<String, Object> policyVersion,
        Double latencyMs,
        Double sampleRate,
        String actorId,
        Long createdAt) {
}
