package net.atlasauth.atlas.mirror;

import java.util.List;

/**
 * What the key mirror keeps per end-user API key: never the secret, only its SHA-256 (lowercase hex).
 *
 * @param id             the Atlas key id
 * @param hash           sha256 of the full secret, lowercase hex ({@code legacy:<id>} for a bcrypt/argon2id import not yet seen)
 * @param prefix         the public head, for listings
 * @param scopes         the key's scopes, null for none
 * @param allowedIps     addresses / CIDR ranges the key may be used from, null/empty = any
 * @param expiresAt      epoch ms, null = never
 * @param revokedAt      epoch ms, null = live
 * @param organizationId the organization a user key is bound to, null = unbound
 * @param revokedReason  why it was revoked, when Atlas said
 */
public record MirroredApiKey(
        String id,
        String hash,
        String prefix,
        String subjectType,
        String subjectId,
        String name,
        List<String> scopes,
        List<String> allowedIps,
        Long expiresAt,
        Long revokedAt,
        String organizationId,
        String revokedReason) {

    public MirroredApiKey withRevoked(Long at, String reason) {
        return new MirroredApiKey(id, hash, prefix, subjectType, subjectId, name, scopes, allowedIps, expiresAt, at, organizationId, reason);
    }

    public MirroredApiKey withHash(String newHash) {
        return new MirroredApiKey(id, newHash, prefix, subjectType, subjectId, name, scopes, allowedIps, expiresAt, revokedAt, organizationId, revokedReason);
    }
}
