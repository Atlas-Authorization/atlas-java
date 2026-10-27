package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/app_catalog/installed}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AppCatalogInstalledResponse(
        String object,
        List<AppCatalogInstalledResponseDataItem> data) {
}
