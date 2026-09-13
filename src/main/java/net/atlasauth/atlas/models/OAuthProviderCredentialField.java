package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One credential input a provider needs (client_id, team_id, private key…). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthProviderCredentialField(
        String key,
        String label,
        /** Stored encrypted and never returned. */
        Boolean secret,
        Boolean multiline,
        Boolean optional,
        String help) {
}
