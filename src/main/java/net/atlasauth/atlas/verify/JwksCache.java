package net.atlasauth.atlas.verify;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.LongSupplier;

/**
 * §7.3: "JWKS cached in-process with kid-miss refetch (max 1/min) so key
 * rotation needs no deploys."
 *
 * <p>The rate limit on the refetch is the security property, not a politeness.
 * Without it an attacker sends tokens carrying random {@code kid} values and each
 * forces an outbound request to the JWKS endpoint — turning any unauthenticated
 * caller into a traffic amplifier from inside the customer's own infrastructure.
 * The cache's job is as much to refuse to fetch as it is to fetch. It also keeps
 * serving stale keys through a JWKS outage, so a failed refetch degrades to "new
 * keys do not work yet" rather than "nobody can authenticate".
 */
public final class JwksCache {

    /** §7.3: at most one refetch per minute, however many misses arrive. */
    public static final long REFETCH_INTERVAL_MS = 60_000L;
    /** §7.3 serves {@code Cache-Control: max-age=3600}; honoured rather than ignored. */
    public static final long DEFAULT_TTL_MS = 3_600_000L;

    public enum Outcome { FRESH, CACHED, REFETCHED, THROTTLED, FAILED }

    private final String url;
    private final HttpClient http;
    private final LongSupplier now;
    private final long ttlMs;
    private final long refetchIntervalMs;

    private JWKSet cached;
    private long fetchedAt;
    private long lastAttemptAt;
    private volatile Outcome lastOutcome = Outcome.FRESH;

    public JwksCache(String url, HttpClient http, LongSupplier now) {
        this(url, http, now, DEFAULT_TTL_MS, REFETCH_INTERVAL_MS);
    }

    public JwksCache(String url, HttpClient http, LongSupplier now, long ttlMs, long refetchIntervalMs) {
        this.url = url;
        this.http = http;
        this.now = now != null ? now : System::currentTimeMillis;
        this.ttlMs = ttlMs;
        this.refetchIntervalMs = refetchIntervalMs;
    }

    public Outcome lastOutcome() {
        return lastOutcome;
    }

    private boolean has(String kid) {
        if (cached == null) {
            return false;
        }
        if (kid == null) {
            return !cached.getKeys().isEmpty();
        }
        for (JWK key : cached.getKeys()) {
            if (kid.equals(key.getKeyID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * The JWKS to verify against, refetching if this {@code kid} is unknown.
     * Returns whatever is cached when a refetch is throttled or fails.
     */
    public synchronized JWKSet get(String kid) {
        long t = now.getAsLong();
        boolean expired = cached == null || t - fetchedAt >= ttlMs;
        boolean kidMiss = cached != null && !has(kid);

        if (!expired && !kidMiss) {
            lastOutcome = Outcome.CACHED;
            return cached;
        }

        // The throttle: a kid-miss inside the window is answered from cache and
        // the token simply fails to verify.
        if (kidMiss && !expired && t - lastAttemptAt < refetchIntervalMs) {
            lastOutcome = Outcome.THROTTLED;
            return cached;
        }

        lastAttemptAt = t;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("accept", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("JWKS fetch failed (" + response.statusCode() + ")");
            }
            JWKSet parsed = JWKSet.parse(response.body());
            cached = parsed;
            fetchedAt = t;
            lastOutcome = kidMiss ? Outcome.REFETCHED : Outcome.FRESH;
            return cached;
        } catch (Exception e) {
            // Keep serving what we have.
            lastOutcome = Outcome.FAILED;
            return cached;
        }
    }
}
