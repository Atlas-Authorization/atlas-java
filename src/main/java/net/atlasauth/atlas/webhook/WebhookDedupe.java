package net.atlasauth.atlas.webhook;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * Verify an Atlas webhook (signature and timestamp, exactly as {@link WebhookVerifier}) and drop an event
 * whose {@code atlas-id} was already seen: Atlas retries a delivery until your endpoint answers 2xx, and every
 * retry of one event carries the same id.
 *
 * <pre>{@code
 * WebhookDedupe dedupe = new WebhookDedupe(new WebhookVerifier(secret), WebhookDedupe.memoryStore());
 * WebhookDedupe.Result r = dedupe.verify(rawBody, headers);   // throws WebhookDedupe.VerificationException
 * if (!r.duplicate()) {
 *     try { handle(r.event()); } catch (RuntimeException e) { r.release(); throw e; }  // let Atlas's retry through
 * }
 * // answer 200
 * }</pre>
 */
public final class WebhookDedupe {

    /**
     * Where seen delivery ids live. {@link #setIfAbsent} must be atomic: it stores the key for the TTL and
     * returns true when the key was NOT there (Redis: {@code SET key 1 EX ttl NX}).
     */
    public interface Store {
        boolean setIfAbsent(String key, long ttlSeconds);

        /** Forget a key, so a failed handler's retry is processed. Optional. */
        default void delete(String key) {
        }
    }

    /** The result of {@link #verify}. */
    public static final class Result {
        private final JsonNode event;
        private final boolean duplicate;
        private final String deliveryId;
        private final Runnable release;

        Result(JsonNode event, boolean duplicate, String deliveryId, Runnable release) {
            this.event = event;
            this.duplicate = duplicate;
            this.deliveryId = deliveryId;
            this.release = release;
        }

        /** The parsed envelope {@code {id, type, timestamp, instance_id, data}}. */
        public JsonNode event() {
            return event;
        }

        /** True when this {@code atlas-id} was already seen: acknowledge with 2xx and do nothing. */
        public boolean duplicate() {
            return duplicate;
        }

        /** The signed {@code atlas-id}, identical on every retry of one event. */
        public String deliveryId() {
            return deliveryId;
        }

        /** Forget this delivery (when the store supports delete) so a retry is processed, e.g. after your handler failed. */
        public void release() {
            release.run();
        }
    }

    /** A delivery that does not verify (answer 400/401). An unverified delivery never touches the store. */
    public static final class VerificationException extends RuntimeException {
        private final String reason;

        public VerificationException(String reason) {
            super("Atlas webhook verification failed: " + reason);
            this.reason = reason;
        }

        /** {@code missing_headers}, {@code malformed_signature}, {@code stale_timestamp}, {@code signature_mismatch} or {@code invalid_body}. */
        public String reason() {
            return reason;
        }
    }

    private final WebhookVerifier verifier;
    private final Store store;
    private final long ttlSeconds;
    private final String keyPrefix;

    public WebhookDedupe(WebhookVerifier verifier, Store store) {
        this(verifier, store, 86_400, "atlas-webhook:");
    }

    /**
     * @param ttlSeconds how long a delivery id is remembered (keep it longer than your endpoint's retry window)
     * @param keyPrefix  key prefix in the store
     */
    public WebhookDedupe(WebhookVerifier verifier, Store store, long ttlSeconds, String keyPrefix) {
        this.verifier = verifier;
        this.store = store;
        this.ttlSeconds = ttlSeconds;
        this.keyPrefix = keyPrefix;
    }

    /** Verify the RAW body and headers, then claim the delivery id. */
    public Result verify(byte[] rawBody, Map<String, ?> headers) {
        return claim(verifier.verify(rawBody, headers));
    }

    /** As {@link #verify(byte[], Map)}, also checking a {@code v2} signature against the exact URL Atlas POSTed to. */
    public Result verify(byte[] rawBody, Map<String, ?> headers, String endpointUrl, boolean requireV2) {
        return claim(verifier.verify(rawBody, headers, endpointUrl, requireV2));
    }

    private Result claim(WebhookVerification v) {
        if (!v.verified() || v.deliveryId() == null) {
            throw new VerificationException(v.reason() != null ? v.reason() : "signature_mismatch");
        }
        if (v.event() == null) {
            throw new VerificationException("invalid_body");
        }
        String key = keyPrefix + v.deliveryId();
        boolean fresh = store.setIfAbsent(key, ttlSeconds);
        return new Result(v.event(), !fresh, v.deliveryId(), () -> store.delete(key));
    }

    /** A single-process store. Use a shared one (Redis, a database) when you run more than one instance. */
    public static Store memoryStore() {
        return memoryStore(System::currentTimeMillis);
    }

    public static Store memoryStore(LongSupplier now) {
        Map<String, Long> seen = new ConcurrentHashMap<>();
        return new Store() {
            @Override
            public boolean setIfAbsent(String key, long ttlSeconds) {
                long t = now.getAsLong();
                seen.values().removeIf(exp -> exp <= t);
                return seen.putIfAbsent(key, t + ttlSeconds * 1000) == null;
            }

            @Override
            public void delete(String key) {
                seen.remove(key);
            }
        };
    }

    /**
     * A {@link Store} on SQL over JDBC. Create the table once:
     *
     * <pre>
     * postgres: create table atlas_webhook_seen (key text primary key, expires_at timestamptz not null);
     * sqlite:   create table atlas_webhook_seen (key text primary key, expires_at integer not null);  -- epoch ms
     * mysql:    create table atlas_webhook_seen (`key` varchar(255) primary key, expires_at datetime not null);
     * </pre>
     *
     * A key whose row has expired counts as unseen and is claimed again.
     */
    public static Store jdbcStore(javax.sql.DataSource dataSource, Dialect dialect) {
        return new JdbcStore(dataSource, dialect);
    }

    public enum Dialect { POSTGRES, SQLITE, MYSQL }

    private static final class JdbcStore implements Store {
        private final javax.sql.DataSource ds;
        private final Dialect dialect;

        JdbcStore(javax.sql.DataSource ds, Dialect dialect) {
            this.ds = ds;
            this.dialect = dialect;
        }

        @Override
        public boolean setIfAbsent(String key, long ttlSeconds) {
            try (java.sql.Connection c = ds.getConnection()) {
                switch (dialect) {
                    case SQLITE: {
                        long now = System.currentTimeMillis();
                        try (java.sql.PreparedStatement ps = c.prepareStatement("insert into atlas_webhook_seen (key, expires_at) values (?, ?) "
                                + "on conflict (key) do update set expires_at = excluded.expires_at where atlas_webhook_seen.expires_at <= ?")) {
                            ps.setString(1, key);
                            ps.setLong(2, now + ttlSeconds * 1000);
                            ps.setLong(3, now);
                            return ps.executeUpdate() > 0;
                        }
                    }
                    case MYSQL: {
                        try (java.sql.PreparedStatement del = c.prepareStatement("delete from atlas_webhook_seen where `key` = ? and expires_at <= now()")) {
                            del.setString(1, key);
                            del.executeUpdate();
                        }
                        try (java.sql.PreparedStatement ps = c.prepareStatement("insert ignore into atlas_webhook_seen (`key`, expires_at) values (?, now() + interval ? second)")) {
                            ps.setString(1, key);
                            ps.setLong(2, ttlSeconds);
                            return ps.executeUpdate() > 0;
                        }
                    }
                    default: {
                        try (java.sql.PreparedStatement ps = c.prepareStatement("insert into atlas_webhook_seen (key, expires_at) values (?, now() + (? || ' seconds')::interval) "
                                + "on conflict (key) do update set expires_at = excluded.expires_at where atlas_webhook_seen.expires_at <= now()")) {
                            ps.setString(1, key);
                            ps.setString(2, String.valueOf(ttlSeconds));
                            return ps.executeUpdate() > 0;
                        }
                    }
                }
            } catch (java.sql.SQLException e) {
                throw new IllegalStateException("webhook dedupe store failed: " + e.getMessage(), e);
            }
        }

        @Override
        public void delete(String key) {
            try (java.sql.Connection c = ds.getConnection();
                 java.sql.PreparedStatement ps = c.prepareStatement(dialect == Dialect.MYSQL ? "delete from atlas_webhook_seen where `key` = ?"
                         : "delete from atlas_webhook_seen where key = ?")) {
                ps.setString(1, key);
                ps.executeUpdate();
            } catch (java.sql.SQLException e) {
                throw new IllegalStateException("webhook dedupe store failed: " + e.getMessage(), e);
            }
        }
    }
}
