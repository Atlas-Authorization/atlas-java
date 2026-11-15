package net.atlasauth.atlas.mirror;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** An in-memory {@link KeyMirrorStore}, for tests and single-process apps. */
public final class MemoryKeyMirrorStore implements KeyMirrorStore {

    private final Map<String, MirroredApiKey> keys = new ConcurrentHashMap<>();
    private volatile String cursor;
    private volatile Long lastSyncAt;

    @Override
    public void put(MirroredApiKey key) {
        keys.put(key.id(), key);
    }

    @Override
    public MirroredApiKey get(String id) {
        return keys.get(id);
    }

    @Override
    public MirroredApiKey getByHash(String hash) {
        return keys.values().stream().filter(k -> k.hash().equals(hash)).findFirst().orElse(null);
    }

    @Override
    public void markRevoked(String id, long revokedAt, String reason) {
        keys.computeIfPresent(id, (k, v) -> v.revokedAt() == null ? v.withRevoked(revokedAt, reason) : v);
    }

    @Override
    public boolean markMembershipEnded(String userId, String organizationId, long revokedAt) {
        keys.replaceAll((id, k) -> k.revokedAt() == null && "user".equals(k.subjectType()) && userId.equals(k.subjectId())
                && organizationId.equals(k.organizationId()) ? k.withRevoked(revokedAt, "membership_ended") : k);
        return true;
    }

    @Override
    public String getCursor() {
        return cursor;
    }

    @Override
    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    @Override
    public Long getLastSyncAt() {
        return lastSyncAt;
    }

    @Override
    public void setLastSyncAt(long at) {
        this.lastSyncAt = at;
    }
}
