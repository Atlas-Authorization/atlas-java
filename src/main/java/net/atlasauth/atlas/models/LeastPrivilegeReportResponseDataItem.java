package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LeastPrivilegeReportResponseDataItem(
        String object,
        String subjectType,
        String subjectId,
        String name,
        Long windowDays,
        Long observedDays,
        String confidence,
        List<Map<String, Object>> granted,
        List<Map<String, Object>> used,
        List<Map<String, Object>> unused,
        Map<String, Object> suggestion,
        Boolean applicable,
        String note) {
}
