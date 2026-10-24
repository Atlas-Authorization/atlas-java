package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A credential an integrated partner service holds on this instance. {@code clientSecret} is set once, on
 * create and rotate; a revoke carries {@code revoked}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PartnerLink(
        String object,
        String id,
        String clientId,
        /** Any lowercase id; cookiemunch, chatly, forumulate and referalli are presets. */ String partner,
        String name,
        boolean preset,
        String resource,
        List<String> scopes,
        String clientSecretPrefix,
        String tokenEndpoint,
        /** active, expired or revoked. */ String status,
        Long expiresAt,
        Long revokedAt,
        Long lastUsedAt,
        String lastUsedIp,
        @JsonProperty("calls_30d") int calls30d,
        long createdAt,
        String clientSecret,
        Boolean revoked,
        /** With an `X-Secret-Recipient`: the secret sealed to it (then the plaintext field is absent). */ String sealedSecret,
        /** With an `X-Secret-Recipient`: the client secret sealed to it. */ String sealedClientSecret,
        /** With an `X-Secret-Recipient`: the webhook secret sealed to it. */ String sealedWebhookSecret,
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed) {
    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public PartnerLink(String object, String id, String clientId, String partner, String name, boolean preset, String resource, List<String> scopes, String clientSecretPrefix, String tokenEndpoint, String status, Long expiresAt, Long revokedAt, Long lastUsedAt, String lastUsedIp, int calls30d, long createdAt, String clientSecret, Boolean revoked) {
        this(object, id, clientId, partner, name, preset, resource, scopes, clientSecretPrefix, tokenEndpoint, status, expiresAt, revokedAt, lastUsedAt, lastUsedIp, calls30d, createdAt, clientSecret, revoked, null, null, null, null);
    }

}
