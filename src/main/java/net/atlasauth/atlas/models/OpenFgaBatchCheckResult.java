package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFgaBatchCheckResult(
        Map<String, OpenFgaCheckResult> result) {
}
