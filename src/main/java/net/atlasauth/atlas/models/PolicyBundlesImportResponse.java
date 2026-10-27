package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/policy_bundles/import}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicyBundlesImportResponse(
        String object,
        Boolean dryRun,
        Boolean applied,
        Boolean prune,
        String revision,
        PolicyBundlesImportResponseDiff diff) {
}
