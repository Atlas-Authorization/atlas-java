package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A scoped instance secret key. A create or rotate response also carries {@code secret} exactly once;
 * a rotation carries {@code replaces} and {@code previousKeyExpiresAt}; a revoke carries {@code revoked}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SecretKey(
        String object,
        String id,
        String kind,
        String format,
        String prefix,
        String name,
        String owner,
        List<String> scopes,
        String environment,
        ResourceRef resource,
        Long expiresAt,
        Integer expiresInDays,
        /** active, expiring, expired or revoked. */ String status,
        List<String> ipAllowlist,
        List<String> originAllowlist,
        Long lastUsedAt,
        String lastUsedIp,
        long createdAt,
        Long revokedAt,
        List<String> allowedIps,
        String rotatedFrom,
        /** The key, shown once (absent on a read). */ String secret,
        /** Note about the one-time secret. */ String note,
        /** On a rotation: the id of the key this one replaces. */ String replaces,
        /** On a rotation: when the previous key stops working (epoch ms). */ Long previousKeyExpiresAt,
        /** On a revoke: true. */ Boolean revoked) {
}
