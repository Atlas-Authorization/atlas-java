package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/policy_bundles/verify}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicyBundlesVerifyResponse(
        String object,
        Boolean valid,
        PolicyBundlesVerifyResponseSignature signature,
        PolicyBundlesVerifyResponseIntegrity integrity,
        String revision,
        Map<String, Object> manifest) {
}
