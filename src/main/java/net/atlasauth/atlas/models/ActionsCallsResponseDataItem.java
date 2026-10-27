package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActionsCallsResponseDataItem(
        String object,
        String id,
        String actionId,
        String trigger,
        String userId,
        Boolean test,
        String status,
        Long httpStatus,
        Long latencyMs,
        String error,
        Map<String, Object> answer,
        Long createdAt) {
}
