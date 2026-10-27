package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/service_connector_catalog}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ServiceConnectorsCatalogResponse(
        String object,
        List<ServiceConnectorsCatalogResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
