package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SessionExchangeApp(
        String object,
        String id,
        String name,
        String backchannelLogoutUri,
        String audience,
        Long createdAt,
        Long updatedAt) {
}
