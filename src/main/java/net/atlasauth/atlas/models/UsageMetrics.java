package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UsageMetrics(
        String metric,
        String label,
        String description,
        Boolean additive,
        List<JsonNode> points,
        JsonNode peak,
        Double total,
        Double trend) {
}
