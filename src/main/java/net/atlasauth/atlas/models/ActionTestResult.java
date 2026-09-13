package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** What an action produced against a sample event — no real data is touched. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActionTestResult(
        String object,
        String id,
        boolean denied,
        String denyReason,
        Map<String, Object> accessTokenClaims,
        Map<String, Object> idTokenClaims,
        Map<String, Object> appMetadata,
        List<String> logs,
        String error) {
}
