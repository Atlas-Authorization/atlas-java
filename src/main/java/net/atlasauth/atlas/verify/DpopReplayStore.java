package net.atlasauth.atlas.verify;

/**
 * Guards against a DPoP proof being used twice. Back it with something every instance of your API
 * shares (Redis {@code SET key 1 NX PX ttl}, a unique database key) when it runs on more than one;
 * {@link MemoryDpopReplayStore} protects a single process.
 */
@FunctionalInterface
public interface DpopReplayStore {
    /**
     * Record {@code key} until {@code expiresAtMillis} (epoch milliseconds). Return {@code true} the
     * first time it is seen, {@code false} when it was already recorded and has not expired.
     */
    boolean claim(String key, long expiresAtMillis);
}
