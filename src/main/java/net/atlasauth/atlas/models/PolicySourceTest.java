package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** {@code POST /v1/instance/policy_source/test}: a signed fetch of the feed, validated but not stored. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicySourceTest(
        String object,
        Boolean ok,
        Integer httpStatus,
        Integer latencyMs,
        String error,
        List<String> warnings,
        List<PolicySourceTestPolicy> policies,
        PolicySourceDelivery delivery) {
}
