package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/abac_policies/{id}/diff}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacDiffResponse(
        String object,
        String policyId,
        Long from,
        Long to,
        AbacDiffResponseText text,
        AbacDiffResponseSemantic semantic) {
}
