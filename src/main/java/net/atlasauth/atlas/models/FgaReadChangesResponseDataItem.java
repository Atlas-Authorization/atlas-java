package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaReadChangesResponseDataItem(
        String object,
        String operation,
        String user,
        String relation,
        String target,
        Map<String, Object> condition,
        String consistencyToken,
        Long createdAt) {
}
