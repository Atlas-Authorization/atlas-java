package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrgRoleGrantsListDelegationsResponseDataItem(
        String object,
        String id,
        String grantId,
        String userId,
        String roleKey,
        Long createdAt) {
}
