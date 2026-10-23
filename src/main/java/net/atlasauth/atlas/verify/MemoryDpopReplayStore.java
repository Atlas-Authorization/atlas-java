package net.atlasauth.atlas.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongSupplier;

/** The in-process {@link DpopReplayStore}: entries live until they expire, at most {@code maxEntries}. */
public final class MemoryDpopReplayStore implements DpopReplayStore {

    private final int max;
    private final LongSupplier now;
    private final Map<String, Long> seen = new HashMap<>();

    public MemoryDpopReplayStore() {
        this(10_000, System::currentTimeMillis);
    }

    /** @param now clock returning epoch milliseconds */
    public MemoryDpopReplayStore(int maxEntries, LongSupplier now) {
        this.max = maxEntries > 0 ? maxEntries : 10_000;
        this.now = now != null ? now : System::currentTimeMillis;
    }

    @Override
    public synchronized boolean claim(String key, long expiresAtMillis) {
        long t = now.getAsLong();
        Long prior = seen.get(key);
        if (prior != null && prior > t) {
            return false;
        }
        seen.put(key, expiresAtMillis);
        if (seen.size() > max) {
            seen.values().removeIf(exp -> exp <= t);
            while (seen.size() > max) {
                String oldest = null;
                long oldestAt = Long.MAX_VALUE;
                for (Map.Entry<String, Long> e : seen.entrySet()) {
                    if (!e.getKey().equals(key) && e.getValue() < oldestAt) {
                        oldest = e.getKey();
                        oldestAt = e.getValue();
                    }
                }
                if (oldest == null) {
                    break;
                }
                seen.remove(oldest);
            }
        }
        return true;
    }

    /** How many entries are held. */
    public synchronized int size() {
        return seen.size();
    }
}
