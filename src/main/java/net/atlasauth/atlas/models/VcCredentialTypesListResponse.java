package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/vc/credential_types}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VcCredentialTypesListResponse(
        String object,
        List<VcCredentialTypesListResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
