package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The result of {@code oauthClients().logout(id, sid)}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientLogout(
        String object,
        String clientId,
        String sid,
        /** {@code revoked}: this call ended the Atlas session. {@code already_ended}: it had already ended. */ String session,
        /** This client's refresh grants under the sid that were revoked (offline_access included). */ int grantsRevoked,
        /** Back-channel logouts queued, to this client and every other app signed in through the session. */ int backchannelLogouts) {
}
