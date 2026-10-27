package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/iga/birthright_rules}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaLifecycleListBirthrightRulesResponse(
        String object,
        List<IgaLifecycleListBirthrightRulesResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
