package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginReadinessChecks(
        String name,
        Boolean pass,
        JsonNode observed,
        String detail,
        /** False when the check does not matter for the assumed integration (it then never makes {@code ready} false); null from older servers. */ Boolean applies) {
    /** The 1.4 shape, without {@code applies}. */
    public LoginReadinessChecks(String name, Boolean pass, JsonNode observed, String detail) {
        this(name, pass, observed, detail, null);
    }
}
