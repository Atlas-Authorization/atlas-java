package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RequestInspectionFindings(
        String check,
        Boolean blocks,
        JsonNode observed,
        String detail) {
}
