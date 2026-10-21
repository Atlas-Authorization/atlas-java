package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One {@code allowed_origins} entry that may also declare how the origin signs in. With no
 * integration it is sent as the plain origin string (which keeps the hint the origin already
 * had); otherwise as {@code {"origin", "integration"}}.
 *
 * @param origin      the bare origin, e.g. {@code https://app.example.com}
 * @param integration {@link #REDIRECT} (hosted-redirect OIDC: the app never runs the embedded
 *                    components), {@link #EMBEDDED} (the sign-in components run on the origin),
 *                    {@link #AUTO} (clear the hint; Atlas infers it) or null (keep it)
 */
public record AllowedOriginEntry(String origin, String integration) {
    public static final String REDIRECT = "redirect";
    public static final String EMBEDDED = "embedded";
    public static final String AUTO = "auto";

    /** A plain origin: keeps its current hint. */
    public static AllowedOriginEntry of(String origin) {
        return new AllowedOriginEntry(origin, null);
    }

    /** An origin with a declared integration. */
    public static AllowedOriginEntry of(String origin, String integration) {
        return new AllowedOriginEntry(origin, integration);
    }

    @JsonValue
    Object toWire() {
        if (integration == null) return origin;
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("origin", origin);
        m.put("integration", integration);
        return m;
    }
}
