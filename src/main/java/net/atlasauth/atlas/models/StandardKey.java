package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The shape every credential listing uses ({@code GET /v1/keys}, {@code GET /v1/platform/keys}). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StandardKey(
        /** {@code key} or {@code secret_key}. */ String object,
        String id,
        /** secret_key, platform_key, account_key, webhook_secret, partner_link or access_token. */ String kind,
        /** {@code atlas} ({@code atlas_<env>_<kind>_...}) or {@code legacy} ({@code sk_}, {@code psk_}, {@code whsec_}, {@code dsk_}). */ String format,
        String prefix,
        String name,
        String owner,
        List<String> scopes,
        /** live, test or null. */ String environment,
        ResourceRef resource,
        Long expiresAt,
        Integer expiresInDays,
        /** active, expiring, expired or revoked. */ String status,
        List<String> ipAllowlist,
        List<String> originAllowlist,
        Long lastUsedAt,
        String lastUsedIp,
        long createdAt,
        Long revokedAt) {
}
