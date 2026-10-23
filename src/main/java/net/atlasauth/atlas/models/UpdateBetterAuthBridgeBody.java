package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@code PUT /v1/migrations/better_auth/bridge}: every field is optional and merged onto the stored
 * settings. Clear {@code ends_at} with {@code set("ends_at", null)}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class UpdateBetterAuthBridgeBody {
    /** Whether the exchange accepts Better Auth sessions. */
    public Boolean enabled;
    /** End of the migration window (ISO-8601 or Unix ms). */
    public Object endsAt;
    /** Better Auth's {@code advanced.cookiePrefix} (default {@code better-auth}). */
    public String cookiePrefix;
    /** Ask the old app about sessions missing from the import. */
    public LiveFallback liveFallback;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateBetterAuthBridgeBody() {
    }

    public UpdateBetterAuthBridgeBody enabled(Boolean v) { this.enabled = v; return this; }
    /** ISO-8601 string or Unix milliseconds. */
    public UpdateBetterAuthBridgeBody endsAt(Object v) { this.endsAt = v; return this; }
    public UpdateBetterAuthBridgeBody cookiePrefix(String v) { this.cookiePrefix = v; return this; }
    public UpdateBetterAuthBridgeBody liveFallback(LiveFallback v) { this.liveFallback = v; return this; }

    /** Set any field by its wire name, including an explicit {@code null}. */
    public UpdateBetterAuthBridgeBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }

    /**
     * The live fallback. {@code secret} (the old app's {@code BETTER_AUTH_SECRET}) is write-only: leave it
     * unset to keep the stored one, or {@link #clearSecret()} to remove it.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class LiveFallback {
        public Boolean enabled;
        public String baseUrl;
        public String secret;

        @JsonIgnore
        private final Map<String, Object> additionalFields = new LinkedHashMap<>();

        public LiveFallback enabled(Boolean v) { this.enabled = v; return this; }
        public LiveFallback baseUrl(String v) { this.baseUrl = v; return this; }
        public LiveFallback secret(String v) { this.secret = v; this.additionalFields.remove("secret"); return this; }
        /** Send {@code secret: null}: the stored secret is deleted. */
        public LiveFallback clearSecret() { this.secret = null; this.additionalFields.put("secret", null); return this; }
        public LiveFallback set(String key, Object value) { this.additionalFields.put(key, value); return this; }

        @JsonAnyGetter
        public Map<String, Object> additionalFields() { return additionalFields; }
    }
}
