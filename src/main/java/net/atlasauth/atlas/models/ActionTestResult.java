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
        String error,
        /** What the code queued with api.http (sent only on a real post_login / post_user_registration run). */ List<ActionTestHttpRequest> httpRequests) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public ActionTestResult(String object, String id, boolean denied, String denyReason, Map<String, Object> accessTokenClaims, Map<String, Object> idTokenClaims, Map<String, Object> appMetadata, List<String> logs, String error) {
        this(object, id, denied, denyReason, accessTokenClaims, idTokenClaims, appMetadata, logs, error, null);
    }
}
