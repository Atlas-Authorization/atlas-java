package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/connections/import}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectionsImportConnectionsResponse(
        String object,
        Long total,
        Boolean dryRun,
        Boolean validated,
        Long created,
        Long updated,
        Long wouldCreate,
        Long wouldUpdate,
        Long failed,
        List<ConnectionsImportConnectionsResponseDataItem> data) {
}
