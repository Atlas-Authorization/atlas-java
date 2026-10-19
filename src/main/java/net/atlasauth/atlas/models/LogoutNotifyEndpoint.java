package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A logout fan-out endpoint: gets the same signed logout_token as your OIDC apps. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LogoutNotifyEndpoint(
        String object,
        String id,
        String url,
        /** The client_id it follows; null = instance-wide. */ String oauthClientId,
        /** The aud its logout_tokens carry. */ String audience,
        String description,
        long createdAt,
        long updatedAt) {
}
