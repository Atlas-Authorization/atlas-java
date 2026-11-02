package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@code POST /v1/oauth_clients/{id}/secret/reveal}: the secret the latest create or rotate
 * generated, once more (a single time, within 10 minutes, while it is still current).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientSecretReveal(
        /** {@code oauth_client_secret}. */ String object,
        String id,
        String clientId,
        String clientSecret,
        String secretPrefix,
        /** When the secret stops authenticating (epoch ms); null = never. */ Long clientSecretExpiresAt,
        String note) {
}
