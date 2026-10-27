package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/least_privilege/apply}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LeastPrivilegeApplyResponse(
        String object,
        String subjectType,
        String subjectId,
        Boolean applied,
        String summary,
        List<LeastPrivilegeApplyResponseChangesItem> changes) {
}
