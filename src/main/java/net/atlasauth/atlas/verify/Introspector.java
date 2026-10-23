package net.atlasauth.atlas.verify;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.LongSupplier;

/**
 * RFC 7662 token introspection at Atlas, for opaque (reference) access tokens.
 *
 * <p>An API, client or instance set to the {@code opaque} access-token format gets
 * {@code oat_…} tokens only Atlas can read. The introspector asks Atlas with your API's own
 * client credentials ({@code client_secret_basic}) and caches the answer by the token's
 * SHA-256 (the token itself is never kept). The client must be a confidential client the
 * API lists in {@code introspection_client_ids} (or the client the tokens are issued to).
 */
public final class Introspector {

    /** Atlas could not be reached, or answered with a non-2xx status. */
    public static final class IntrospectionException extends Exception {
        public IntrospectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private record Entry(Map<String, Object> claims, long untilMs) {
    }

    private static final ObjectMapper JSON = new ObjectMapper();

    private final String endpoint;
    private final String basic;
    private final HttpClient http;
    private final LongSupplier now;
    private final long ttlMs;
    private final long negativeTtlMs;
    private final int max;
    private final boolean introspectJwts;
    private final LinkedHashMap<String, Entry> cache = new LinkedHashMap<>();

    private Introspector(Builder b) {
        if (b.clientId == null || b.clientId.isEmpty() || b.clientSecret == null || b.clientSecret.isEmpty()) {
            throw new IllegalArgumentException("Introspection needs a client id and secret.");
        }
        if (b.issuer == null && b.endpoint == null) {
            throw new IllegalArgumentException("Introspection needs an issuer or an endpoint.");
        }
        this.endpoint = b.endpoint != null ? b.endpoint : b.issuer.replaceAll("/+$", "") + "/oauth2/introspect";
        String creds = URLEncoder.encode(b.clientId, StandardCharsets.UTF_8) + ":"
                + URLEncoder.encode(b.clientSecret, StandardCharsets.UTF_8);
        this.basic = "Basic " + Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
        this.http = b.http != null ? b.http : HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        this.now = b.now != null ? b.now : System::currentTimeMillis;
        this.ttlMs = b.cacheTtl.toMillis();
        this.negativeTtlMs = b.negativeCacheTtl.toMillis();
        this.max = b.cacheMax;
        this.introspectJwts = b.introspectJwts;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Whether JWT access tokens are introspected too. */
    public boolean introspectJwts() {
        return introspectJwts;
    }

    static String cacheKey(String token) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(d);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** How many answers are cached. */
    public synchronized int cacheSize() {
        return cache.size();
    }

    /**
     * The claims of an active access token, or {@code null} for an inactive one (also for a
     * refresh token).
     */
    public Map<String, Object> introspect(String token) throws IntrospectionException {
        String key = cacheKey(token);
        synchronized (this) {
            Entry hit = cache.get(key);
            if (hit != null) {
                if (hit.untilMs() > now.getAsLong()) {
                    return hit.claims();
                }
                cache.remove(key);
            }
        }
        String form = "token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) + "&token_type_hint=access_token";
        HttpRequest req = HttpRequest.newBuilder(URI.create(endpoint))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", basic)
                .header("Accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        HttpResponse<String> res;
        try {
            res = http.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IntrospectionException("Introspection request failed.", e);
        }
        if (res.statusCode() < 200 || res.statusCode() > 299) {
            throw new IntrospectionException("Introspection failed with HTTP " + res.statusCode() + ".", null);
        }
        Map<String, Object> body;
        try {
            body = JSON.readValue(res.body(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IntrospectionException("Introspection answered with invalid JSON.", e);
        }
        Map<String, Object> claims = Boolean.TRUE.equals(body.get("active"))
                && !"refresh_token".equals(body.get("token_use")) ? body : null;
        long t = now.getAsLong();
        long until = t + (claims != null ? ttlMs : negativeTtlMs);
        if (claims != null && claims.get("exp") instanceof Number exp) {
            until = Math.min(until, exp.longValue() * 1000L);
        }
        if (until > t) {
            synchronized (this) {
                cache.remove(key);
                cache.put(key, new Entry(claims, until));
                if (cache.size() > max) {
                    cache.values().removeIf(e -> e.untilMs() <= t);
                    Iterator<String> it = cache.keySet().iterator();
                    while (cache.size() > max && it.hasNext()) {
                        it.next();
                        it.remove();
                    }
                }
            }
        }
        return claims;
    }

    /** Builder for {@link Introspector}. */
    public static final class Builder {
        private String issuer;
        private String clientId;
        private String clientSecret;
        private String endpoint;
        private Duration cacheTtl = Duration.ofSeconds(60);
        private Duration negativeCacheTtl = Duration.ofSeconds(10);
        private int cacheMax = 10_000;
        private boolean introspectJwts;
        private HttpClient http;
        private LongSupplier now;

        public Builder issuer(String v) { this.issuer = v; return this; }
        public Builder clientId(String v) { this.clientId = v; return this; }
        public Builder clientSecret(String v) { this.clientSecret = v; return this; }
        /** Default {@code {issuer}/oauth2/introspect}. */
        public Builder endpoint(String v) { this.endpoint = v; return this; }
        /** How long an active answer is reused (default 60 s; never past the token's {@code exp}). */
        public Builder cacheTtl(Duration v) { this.cacheTtl = v; return this; }
        /** How long an inactive answer is reused (default 10 s; zero disables). */
        public Builder negativeCacheTtl(Duration v) { this.negativeCacheTtl = v; return this; }
        /** Most answers kept (default 10 000). */
        public Builder cacheMax(int v) { this.cacheMax = v; return this; }
        /** Also introspect JWT access tokens, so a revoked grant or a banned user is refused before expiry. */
        public Builder introspectJwts(boolean v) { this.introspectJwts = v; return this; }
        public Builder httpClient(HttpClient v) { this.http = v; return this; }
        /** Clock override (epoch ms), for tests. */
        public Builder now(LongSupplier v) { this.now = v; return this; }

        public Introspector build() {
            return new Introspector(this);
        }
    }
}
