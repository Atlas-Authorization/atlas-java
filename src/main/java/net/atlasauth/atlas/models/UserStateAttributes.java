package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserStateAttributes(
        Map<String, UserStateAttributesProgressionValue> progression,
        JsonNode tier,
        JsonNode flair) {
}
