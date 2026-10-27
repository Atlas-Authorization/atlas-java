package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/flows/{id}/simulate}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JourneysSimulateResponse(
        String object,
        String journeyId,
        Long version,
        String outcome,
        List<Map<String, Object>> effects,
        List<JourneysSimulateResponseTraceItem> trace) {
}
