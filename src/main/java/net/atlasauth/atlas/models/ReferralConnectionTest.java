package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** {@code POST /v1/instance/referrals/test}: ok on 2xx or 404 from the platform. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralConnectionTest(boolean ok, Integer httpStatus, Integer latencyMs, TestError error) {

    /** Why the test failed: e.g. signature_rejected (401/403), timeout, network_error, url_blocked, http_500. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TestError(String code, String message) {
    }
}
