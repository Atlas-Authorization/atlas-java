package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacVersionsResponseDataItem(
        String object,
        String policyId,
        Long version,
        String source,
        List<Map<String, Object>> tests,
        Boolean current,
        Long createdAt) {
}
