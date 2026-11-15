package net.atlasauth.atlas.mirror;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

/**
 * An in-memory set of revoked session ids, polled from {@code GET /v1/sessions/revoked}, so a token whose
 * session Atlas has revoked is rejected within one poll interval even though verification itself is local.
 * Each listed sid is remembered until its token cannot be live any more.
 *
 * <pre>{@code
 * SidRevocationList revoked = SidRevocationList.builder(Backend.of(atlas)).build();
 * SessionClaims claims = verifier.verify(token);
 * if (revoked.isRevoked(claims.sid()) || revoked.unavailable()) { reject(); }
 * }</pre>
 *
 * <p>The secret key needs {@code sessions:read}. Thread-safe: a poll is single-flight.
 */
public final class SidRevocationList {

    private final Backend backend;
    private final Map<String, Long> revoked = new ConcurrentHashMap<>();
    private final long intervalMs;
    private final long lifetimeMs;
    private final boolean rejectWhenStale;
    private final long staleAfterMs;
    private final long serveStaleForMs;
    private final Consumer<Exception> onError;
    private final KeyMirror.StaleListener onServeStale;
    private final LongSupplier now;
    private final long createdAt;
    private final Object pollLock = new Object();
    private volatile Long since;
    private volatile long nextPollAt;
    private volatile Long lastSuccessAt;
    private final String jwksUrl;
    private final String issuer;
    private final java.net.http.HttpClient http;
    private volatile StatusList statusList;
    private volatile long statusListAt;
    private volatile com.fasterxml.jackson.databind.JsonNode jwks;
    private volatile long jwksAt;

    private SidRevocationList(Builder b) {
        this.backend = b.backend;
        this.intervalMs = b.intervalMs;
        this.lifetimeMs = b.maxTokenLifetimeSeconds * 1000;
        this.rejectWhenStale = b.rejectWhenStale;
        this.staleAfterMs = b.staleAfterMs;
        this.serveStaleForMs = b.serveStaleForMs;
        this.onError = b.onError;
        this.onServeStale = b.onServeStale;
        this.now = b.now;
        this.jwksUrl = b.jwksUrl;
        this.issuer = b.issuer;
        this.http = b.http != null ? b.http : java.net.http.HttpClient.newHttpClient();
        this.createdAt = now.getAsLong();
    }

    public static Builder builder(Backend backend) {
        return new Builder(backend);
    }

    /** True when {@code sid} is on the revoked list (polling first when the interval has passed). */
    public boolean isRevoked(String sid) {
        if (jwksUrl != null) {
            String verdict = statusListVerdict(sid);
            if (!verdict.equals("fallback")) {
                return verdict.equals("revoked");
            }
        }
        long t = now.getAsLong();
        if (t >= nextPollAt) {
            synchronized (pollLock) {
                if (now.getAsLong() >= nextPollAt) {
                    poll();
                }
            }
        }
        Long expiry = revoked.get(sid);
        return expiry != null && expiry > t;
    }

    /**
     * True when the list could not be refreshed for {@code staleAfterMs} and the caller asked to fail closed
     * ({@code rejectWhenStale}). Call it after {@link #isRevoked}; a true answer means refuse every token with
     * reason {@code revocation_unavailable}.
     */
    public boolean unavailable() {
        if (!rejectWhenStale) {
            return false;
        }
        Long last = lastSuccessAt;
        long stale = now.getAsLong() - (last != null ? last : createdAt) - staleAfterMs;
        if (stale <= 0) {
            return false;
        }
        if (last != null && stale <= serveStaleForMs) {
            if (onServeStale != null) {
                onServeStale.served(last, stale);
            }
            return false;
        }
        return true;
    }

    /** The signed-list source: "revoked", "ok", or "fallback" when the list cannot answer (use the feed). */
    private String statusListVerdict(String sid) {
        synchronized (pollLock) {
            long t = now.getAsLong();
            if (statusList == null || t - statusListAt >= intervalMs) {
                statusListAt = t; // a failing refresh is not retried until the next interval
                try {
                    Integer window = (int) Math.min(86_400, Math.max(60, lifetimeMs / 1000));
                    String token = backend.getSessionStatusList(window).path("token").asText(null);
                    if (token == null) {
                        throw new IllegalStateException("status list answer had no token");
                    }
                    statusList = StatusList.verify(token, jwks(), issuer, t, 30);
                    lastSuccessAt = t;
                } catch (RuntimeException e) {
                    if (onError != null) {
                        onError.accept(e);
                    }
                }
            }
        }
        StatusList l = statusList;
        if (l == null) {
            return "fallback";
        }
        if (l.has(sid)) {
            return "revoked";
        }
        return l.truncated() || l.expiresAt() + 30_000 < now.getAsLong() ? "fallback" : "ok";
    }

    private com.fasterxml.jackson.databind.JsonNode jwks() {
        long t = now.getAsLong();
        if (jwks == null || t - jwksAt > 600_000) {
            try {
                java.net.http.HttpResponse<String> res = http.send(java.net.http.HttpRequest.newBuilder(java.net.URI.create(jwksUrl))
                        .timeout(java.time.Duration.ofSeconds(10)).GET().build(), java.net.http.HttpResponse.BodyHandlers.ofString());
                if (res.statusCode() != 200) {
                    throw new IllegalStateException("JWKS answered " + res.statusCode());
                }
                jwks = new com.fasterxml.jackson.databind.ObjectMapper().readTree(res.body());
                jwksAt = t;
            } catch (java.io.IOException e) {
                throw new IllegalStateException("JWKS fetch failed: " + e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("interrupted", e);
            }
        }
        return jwks;
    }

    private void poll() {
        long started = now.getAsLong();
        nextPollAt = started + intervalMs;
        try {
            String cursor = null;
            long serverTime = started;
            do {
                JsonNode page = backend.listRevokedSessions(since != null ? since : started - lifetimeMs, cursor, 5000);
                for (JsonNode r : page.path("data")) {
                    revoked.put(r.path("sid").asText(), r.path("revoked_at").asLong() + lifetimeMs);
                }
                serverTime = page.path("server_time").asLong(serverTime);
                cursor = page.path("has_more").asBoolean(false) && page.hasNonNull("next_cursor") ? page.get("next_cursor").asText() : null;
            } while (cursor != null);
            since = serverTime - 10_000;
            lastSuccessAt = started;
            for (Iterator<Map.Entry<String, Long>> it = revoked.entrySet().iterator(); it.hasNext(); ) {
                if (it.next().getValue() <= started) {
                    it.remove();
                }
            }
        } catch (RuntimeException e) {
            // a failed poll never rejects or accepts more than before: the last list stays in force
            if (onError != null) {
                onError.accept(e);
            }
        }
    }

    public static final class Builder {
        private final Backend backend;
        private long intervalMs = 15_000;
        private long maxTokenLifetimeSeconds = 3600;
        private boolean rejectWhenStale;
        private long staleAfterMs = 120_000;
        private long serveStaleForMs;
        private KeyMirror.StaleListener onServeStale;
        private Consumer<Exception> onError;
        private LongSupplier now = System::currentTimeMillis;
        private String jwksUrl;
        private String issuer;
        private java.net.http.HttpClient http;

        /**
         * Use the signed status list ({@code GET /v1/sessions/revocations/status-list}, verified against the instance JWKS and
         * issuer) instead of paging the feed: a smaller answer for a busy verifier. When the list cannot answer (it failed to
         * verify, expired, or was truncated) the feed is used.
         */
        public Builder statusList(String jwksUrl, String issuer) {
            this.jwksUrl = jwksUrl;
            this.issuer = issuer;
            return this;
        }

        public Builder httpClient(java.net.http.HttpClient http) {
            this.http = http;
            return this;
        }

        private Builder(Backend backend) {
            this.backend = backend;
        }

        /** How often to poll. Default 15 s; this plus the token lifetime is the worst-case revocation window. */
        public Builder intervalMs(long intervalMs) {
            this.intervalMs = intervalMs;
            return this;
        }

        /** Your longest-lived session token, in seconds (Atlas's default is 60). A listed sid is remembered this long. Default 3600. */
        public Builder maxTokenLifetimeSeconds(long seconds) {
            this.maxTokenLifetimeSeconds = seconds;
            return this;
        }

        /**
         * Fail closed ({@link #unavailable()} is true) when the list could not be refreshed for {@code staleAfterMs}
         * (default 2 minutes). Default off: keep trusting the last list through an Atlas incident.
         */
        public Builder rejectWhenStale(boolean reject, long staleAfterMs) {
            this.rejectWhenStale = reject;
            this.staleAfterMs = staleAfterMs;
            return this;
        }

        /** With {@code rejectWhenStale}: keep answering from the last list this many ms longer before failing closed. */
        public Builder serveStaleForMs(long ms, KeyMirror.StaleListener listener) {
            this.serveStaleForMs = ms;
            this.onServeStale = listener;
            return this;
        }

        /** Called when a poll fails. */
        public Builder onError(Consumer<Exception> onError) {
            this.onError = onError;
            return this;
        }

        public Builder clock(LongSupplier now) {
            this.now = now;
            return this;
        }

        public SidRevocationList build() {
            return new SidRevocationList(this);
        }
    }
}
