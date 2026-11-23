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
 * @param enabled        false = refused like an unknown key; null = enabled
 * @param credential     {@code secret} or {@code key_pair}; a key-pair row has no secret hash ({@code hash} is a placeholder nothing matches)
 * @param publicKeyPem   key pair: the SPKI PEM assertions are verified against
 * @param audience       key pair: the {@code aud} an assertion must carry
 * @param maxTokenLifetimeSeconds key pair: the longest assertion lifetime, null = 3600
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
        String revokedReason,
        Boolean enabled,
        String credential,
        String publicKeyPem,
        String audience,
        Long maxTokenLifetimeSeconds) {

    /** The shape before the key-pair fields: an enabled secret key. */
    public MirroredApiKey(String id, String hash, String prefix, String subjectType, String subjectId, String name, List<String> scopes,
                          List<String> allowedIps, Long expiresAt, Long revokedAt, String organizationId, String revokedReason) {
        this(id, hash, prefix, subjectType, subjectId, name, scopes, allowedIps, expiresAt, revokedAt, organizationId, revokedReason,
                null, "secret", null, null, null);
    }

    public MirroredApiKey withRevoked(Long at, String reason) {
        return new MirroredApiKey(id, hash, prefix, subjectType, subjectId, name, scopes, allowedIps, expiresAt, at, organizationId, reason,
                enabled, credential, publicKeyPem, audience, maxTokenLifetimeSeconds);
    }

    public MirroredApiKey withHash(String newHash) {
        return new MirroredApiKey(id, newHash, prefix, subjectType, subjectId, name, scopes, allowedIps, expiresAt, revokedAt, organizationId, revokedReason,
                enabled, credential, publicKeyPem, audience, maxTokenLifetimeSeconds);
    }

    public MirroredApiKey withExpiresAt(Long at) {
        return new MirroredApiKey(id, hash, prefix, subjectType, subjectId, name, scopes, allowedIps, at, revokedAt, organizationId, revokedReason,
                enabled, credential, publicKeyPem, audience, maxTokenLifetimeSeconds);
    }

    public MirroredApiKey withEnabled(Boolean value) {
        return new MirroredApiKey(id, hash, prefix, subjectType, subjectId, name, scopes, allowedIps, expiresAt, revokedAt, organizationId, revokedReason,
                value, credential, publicKeyPem, audience, maxTokenLifetimeSeconds);
    }
}
