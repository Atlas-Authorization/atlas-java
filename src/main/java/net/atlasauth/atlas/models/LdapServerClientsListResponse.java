package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/ldap_server_clients}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LdapServerClientsListResponse(
        String object,
        List<LdapServerClientsListResponseDataItem> data,
        Boolean hasMore) {
}
