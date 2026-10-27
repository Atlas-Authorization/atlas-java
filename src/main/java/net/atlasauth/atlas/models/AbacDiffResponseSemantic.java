package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacDiffResponseSemantic(
        Long enumerated,
        Long space,
        Boolean exhaustive,
        Long changed,
        Long newlyAllowed,
        Long newlyDenied,
        Map<String, Object> byAction,
        Boolean truncated,
        List<Map<String, Object>> changes) {
}
