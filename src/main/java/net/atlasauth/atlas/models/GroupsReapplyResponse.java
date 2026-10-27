package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/groups/{id}/reapply}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GroupsReapplyResponse(
        String object,
        String groupId,
        Long matched,
        Long added,
        Long removed) {
}
