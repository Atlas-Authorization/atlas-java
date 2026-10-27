package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaSodListRulesResponseDataItem(
        String object,
        String id,
        String name,
        String description,
        String entitlementA,
        String entitlementB,
        String mode,
        Object approver,
        Boolean enabled,
        Long createdAt,
        Long updatedAt) {
}
