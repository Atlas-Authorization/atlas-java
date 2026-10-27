package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LdapServerClientsListResponseDataItem(
        String object,
        String id,
        String name,
        String baseDn,
        String bindDn,
        String userDnTemplate,
        List<String> allowedCidrs,
        Boolean allowUserBind,
        Long sizeLimit,
        String status,
        String secretPrefix,
        Long lastBindAt,
        Long createdAt,
        String secret) {
}
