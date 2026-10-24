package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** The mint response: the key plus its secret, revealed exactly once. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyWithSecret(
        String object,
        String id,
        String subjectType,
        String subjectId,
        String name,
        String prefix,
        Map<String, Object> claims,
        Long lastUsedAt,
        Long expiresAt,
        Long revokedAt,
        long createdAt,
        /** The {@code ak_} secret, revealed only here. */
        String secret,
        String note,
        /** The key's spend caps; null when it has none. */
        List<ApiKeyLimit> limits,
        List<ApiKeyLimitState> usage,
        /** With an `X-Secret-Recipient`: the secret sealed to it (then the plaintext field is absent). */ String sealedSecret,
        /** With an `X-Secret-Recipient`: the client secret sealed to it. */ String sealedClientSecret,
        /** With an `X-Secret-Recipient`: the webhook secret sealed to it. */ String sealedWebhookSecret,
        /** Names the fields that arrived sealed. */ SecretsSealed secretsSealed) {
    /** The shape before sealedSecret, sealedClientSecret, sealedWebhookSecret, secretsSealed. */
    public ApiKeyWithSecret(String object, String id, String subjectType, String subjectId, String name, String prefix, Map<String, Object> claims, Long lastUsedAt, Long expiresAt, Long revokedAt, long createdAt, String secret, String note, List<ApiKeyLimit> limits, List<ApiKeyLimitState> usage) {
        this(object, id, subjectType, subjectId, name, prefix, claims, lastUsedAt, expiresAt, revokedAt, createdAt, secret, note, limits, usage, null, null, null, null);
    }


    /** The 1.1 shape, without spend caps. */
    public ApiKeyWithSecret(String object, String id, String subjectType, String subjectId, String name, String prefix,
                            Map<String, Object> claims, Long lastUsedAt, Long expiresAt, Long revokedAt, long createdAt,
                            String secret, String note) {
        this(object, id, subjectType, subjectId, name, prefix, claims, lastUsedAt, expiresAt, revokedAt, createdAt,
                secret, note, null, null);
    }
}
