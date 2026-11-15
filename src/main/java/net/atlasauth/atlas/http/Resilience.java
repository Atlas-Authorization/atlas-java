package net.atlasauth.atlas.http;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Status-aware retry policy for the Backend API (opt-in: {@link #none()} is the default, so a client behaves
 * exactly as before unless you ask).
 *
 * <ul>
 *   <li>{@code 429} and {@code 503} are retried for any method: the request was refused, not processed. The
 *       server's {@code Retry-After} (seconds or an HTTP date) sets the wait; without one the wait is a
 *       full-jitter exponential back-off. A {@code Retry-After} longer than {@link #maxDelay()} is not
 *       waited out: the error surfaces so the caller decides.</li>
 *   <li>{@code 502}, {@code 504} and network errors are retried only when a repeat is safe: {@code GET},
 *       {@code PUT}, {@code DELETE}, or a write that carries an {@code Idempotency-Key}.</li>
 *   <li>{@link Builder#statusAware(boolean) statusAware}: before backing off from a {@code 503} or a network
 *       error the client reads the public {@code GET /v1/status}; a confirmed {@code major_outage} stops the
 *       retries at once (waiting cannot help), anything else keeps backing off.</li>
 * </ul>
 */
public final class Resilience {

    /** Sleeps between attempts; replaced in tests so they never wait. */
    @FunctionalInterface
    public interface Sleeper {
        void sleep(Duration d) throws InterruptedException;
    }

    private static final Resilience NONE = new Resilience(0, Duration.ofMillis(500), Duration.ofSeconds(30), false, Resilience::realSleep);

    private final int maxRetries;
    private final Duration baseDelay;
    private final Duration maxDelay;
    private final boolean statusAware;
    private final Sleeper sleeper;

    private Resilience(int maxRetries, Duration baseDelay, Duration maxDelay, boolean statusAware, Sleeper sleeper) {
        this.maxRetries = maxRetries;
        this.baseDelay = baseDelay;
        this.maxDelay = maxDelay;
        this.statusAware = statusAware;
        this.sleeper = sleeper;
    }

    /** No retries (the default). */
    public static Resilience none() {
        return NONE;
    }

    /** Three retries, 500 ms base back-off, 30 s cap, status-aware. */
    public static Resilience standard() {
        return builder().maxRetries(3).statusAware(true).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public int maxRetries() {
        return maxRetries;
    }

    public Duration baseDelay() {
        return baseDelay;
    }

    public Duration maxDelay() {
        return maxDelay;
    }

    public boolean statusAware() {
        return statusAware;
    }

    private static void realSleep(Duration d) throws InterruptedException {
        Thread.sleep(d.toMillis());
    }

    void sleep(Duration d) throws InterruptedException {
        sleeper.sleep(d);
    }

    /** True when the status is one this policy retries for the given method (see the class doc). */
    boolean retryable(int status, String method, boolean hasIdempotencyKey) {
        if (status == 429 || status == 503) {
            return true;
        }
        boolean safe = !"POST".equals(method) && !"PATCH".equals(method) || hasIdempotencyKey;
        return safe && (status == 0 || status == 502 || status == 504);
    }

    /** The wait before retry number {@code attempt} (1-based); {@code null} means do not wait it out. */
    Duration delay(int attempt, Duration retryAfter) {
        if (retryAfter != null) {
            return retryAfter.compareTo(maxDelay) > 0 ? null : retryAfter;
        }
        long cap = Math.min(maxDelay.toMillis(), baseDelay.toMillis() << Math.min(attempt - 1, 20));
        return Duration.ofMillis(cap <= 0 ? 0 : ThreadLocalRandom.current().nextLong(cap / 2, cap + 1));
    }

    /** Parse a {@code Retry-After} header value (delta-seconds or an HTTP date); {@code null} when absent or invalid. */
    public static Duration parseRetryAfter(String value, Instant now) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String v = value.trim();
        try {
            long seconds = Long.parseLong(v);
            return seconds < 0 ? null : Duration.ofSeconds(seconds);
        } catch (NumberFormatException notSeconds) {
            try {
                Instant at = ZonedDateTime.parse(v, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
                Duration d = Duration.between(now, at);
                return d.isNegative() ? Duration.ZERO : d;
            } catch (RuntimeException notDate) {
                return null;
            }
        }
    }

    public static final class Builder {
        private int maxRetries = 3;
        private Duration baseDelay = Duration.ofMillis(500);
        private Duration maxDelay = Duration.ofSeconds(30);
        private boolean statusAware;
        private Sleeper sleeper = Resilience::realSleep;

        /** Retries after the first attempt (0 = none). */
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = Math.max(0, maxRetries);
            return this;
        }

        /** First back-off step; doubles each retry (full jitter). */
        public Builder baseDelay(Duration baseDelay) {
            this.baseDelay = baseDelay;
            return this;
        }

        /** The longest single wait, and the longest {@code Retry-After} that is waited out. */
        public Builder maxDelay(Duration maxDelay) {
            this.maxDelay = maxDelay;
            return this;
        }

        /** Read {@code GET /v1/status} before backing off a 503 or a network error. */
        public Builder statusAware(boolean statusAware) {
            this.statusAware = statusAware;
            return this;
        }

        /** Replace the sleeper (tests). */
        public Builder sleeper(Sleeper sleeper) {
            this.sleeper = sleeper;
            return this;
        }

        public Resilience build() {
            return new Resilience(maxRetries, baseDelay, maxDelay, statusAware, sleeper);
        }
    }
}
