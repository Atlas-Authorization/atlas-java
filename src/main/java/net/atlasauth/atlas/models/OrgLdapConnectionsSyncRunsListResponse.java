package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/org_ldap_connections/{id}/sync_runs}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrgLdapConnectionsSyncRunsListResponse(
        String object,
        List<OrgLdapConnectionsSyncRunsListResponseDataItem> data,
        Boolean hasMore) {
}
