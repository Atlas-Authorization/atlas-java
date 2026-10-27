package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/openfga/stores/{store}/changes}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFgaReadChangesResponse(
        List<OpenFgaReadChangesResponseChangesItem> changes,
        String continuationToken) {
}
