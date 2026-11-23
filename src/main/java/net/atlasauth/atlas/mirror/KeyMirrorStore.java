package net.atlasauth.atlas.mirror;

/**
 * Where the {@link KeyMirror} keeps keys: the smallest interface that works. Back it with a table in
 * production; {@link MemoryKeyMirrorStore} is for tests and single-process apps. Implementations must be
 * thread-safe.
 */
public interface KeyMirrorStore {

    void put(MirroredApiKey key);

    MirroredApiKey get(String id);

    MirroredApiKey getByHash(String hash);

    /** Stamp a key revoked. A no-op for an id the store does not hold or a key already revoked. */
    void markRevoked(String id, long revokedAt, String reason);

    /**
     * Stamp every live key of {@code userId} bound to {@code organizationId} revoked (reason {@code membership_ended}).
     * Optional: a store without it still drops those keys through the revocation feed.
     *
     * @return whether the store supports it
     */
    default boolean markMembershipEnded(String userId, String organizationId, long revokedAt) {
        return false;
    }

    /** The revocation-feed cursor last consumed (null = from the start). */
    String getCursor();

    void setCursor(String cursor);

    /** Epoch ms of the last completed sync, null = never; a store that cannot keep it returns {@code null} and the mirror remembers it in memory. */
    default Long getLastSyncAt() {
        return null;
    }

    default void setLastSyncAt(long at) {
    }

    /**
     * Optional: record an assertion {@code jti} as used until {@code expiresAtMs} and return false when it was already used (a
     * replay). Return null (the default) and the mirror keeps used ids in process memory, which a restart or a second instance does not share.
     */
    default Boolean claimAssertionJti(String keyId, String jti, long expiresAtMs) {
        return null;
    }
}
