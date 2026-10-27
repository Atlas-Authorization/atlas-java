package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/scim_provisioning_presets}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ScimProvisioningPresetsListResponse(
        String object,
        List<ScimProvisioningPresetsListResponseDataItem> data,
        Boolean hasMore) {
}
