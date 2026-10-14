package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RedeemedTicket(
        String object,
        String id,
        JsonNode payload,
        Boolean singleUse) {
}
