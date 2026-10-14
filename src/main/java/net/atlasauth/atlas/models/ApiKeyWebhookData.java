package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * The {@code data} of the {@code api_key.created}, {@code api_key.updated},
 * {@code api_key.revoked} and {@code api_key.limit_reached} webhook events: the key's
 * full non-secret configuration (the secret and its hash are never included). Decode it
 * from {@link WebhookPayload#data()}, e.g. {@code mapper.treeToValue(payload.data(), ApiKeyWebhookData.class)}
 * with a snake_case mapper.
 *
 * @param limit the cap (with its window usage) that filled or refused a debit — only on {@code api_key.limit_reached}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyWebhookData(
        String object,
        String id,
        String prefix,
        String subjectType,
        String subjectId,
        String name,
        Map<String, Object> claims,
        List<String> scopes,
        Map<String, Object> constraints,
        List<ApiKeyLimit> limits,
        Long maxTokenLifetimeSeconds,
        Long expiresAt,
        Long revokedAt,
        Long createdAt,
        Long updatedAt,
        ApiKeyLimitState limit) {
}
