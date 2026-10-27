package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/org_role_grants/effective}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrgRoleGrantsEffectiveResponse(
        String object,
        List<OrgRoleGrantsEffectiveResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
