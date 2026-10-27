package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/app_catalog}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AppCatalogPresetsResponse(
        String object,
        List<AppCatalogPresetsResponseDataItem> data) {
}
