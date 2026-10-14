package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** A user's portable state (progression / tier / flair / entitlements / directives / roles). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserState(
        String object,
        UserStateAttributes attributes,
        List<JsonNode> entitlements,
        List<JsonNode> directives,
        List<JsonNode> roles) {
}
