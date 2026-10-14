package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PreflightFinding(
        String severity,
        String code,
        String message,
        String fix,
        Map<String, String> subject) {
}
