package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/abac/partial_evaluate}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AbacPartialEvaluateResponse(
        String object,
        String decision,
        AbacPartialEvaluateResponseFilter filter,
        AbacPartialEvaluateResponseFilter permit,
        AbacPartialEvaluateResponseFilter forbid,
        List<String> unknowns,
        Boolean relationsResolved,
        AbacPartialEvaluateResponseSql sql,
        Map<String, Object> prisma) {
}
