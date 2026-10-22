package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * {@code POST /v1/instance/referrals/test}: a ping where the provider answers one ({@code method}
 * ping), else a signed probe ({@code method} probe: ok on 2xx or 404 from the platform).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralConnectionTest(
        boolean ok,
        Integer httpStatus,
        Integer latencyMs,
        TestError error,
        /** ping or probe. */ String method,
        /** The ping answer, when the provider supports ping. */ ReferralPing ping,
        /** e.g. a clock more than 60 seconds off. */ List<String> warnings) {

    /** The 1.7 shape, without method, ping and warnings. */
    public ReferralConnectionTest(boolean ok, Integer httpStatus, Integer latencyMs, TestError error) {
        this(ok, httpStatus, latencyMs, error, null, null, null);
    }

    /** Why the test failed: e.g. signature_rejected (401/403), timeout, network_error, url_blocked, http_500. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TestError(String code, String message) {
    }
}
