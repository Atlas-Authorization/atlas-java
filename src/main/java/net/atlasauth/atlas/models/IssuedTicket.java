package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssuedTicket(
        String object,
        String id,
        String ticket,
        Boolean singleUse,
        String boundUser,
        String boundOrg,
        Long createdAt,
        Long expiresAt) {
}
