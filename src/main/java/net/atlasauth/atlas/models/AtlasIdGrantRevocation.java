package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Acknowledges revoking one user's grant to an app. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdGrantRevocation(
        String object,
        String id,
        String sub,
        boolean revoked,
        Integer tokensRevoked,
        Integer backchannelLogouts) {
}
