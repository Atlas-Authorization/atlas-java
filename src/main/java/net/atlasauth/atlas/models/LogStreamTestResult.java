package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The tester's outcome: one synthetic event sent to the sink, its status reported. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LogStreamTestResult(
        String object,
        String id,
        boolean ok,
        Integer status,
        String error) {
}
