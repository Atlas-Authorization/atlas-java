package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A client-to-resource-server grant (the TS {@code ClientGrant}). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientGrant(
        String object,
        String id,
        /** The internal OAuthClient.id (not the public clientId). */
        String clientId,
        String resourceServerId,
        List<String> scopes,
        long createdAt,
        long updatedAt) {
}
