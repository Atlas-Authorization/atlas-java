package net.atlasauth.atlas;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A small, null-tolerant builder for query strings and inline JSON request
 * bodies. Keys whose value is {@code null} are dropped, mirroring how the wire
 * omits absent optionals, so a resource method can offer optional parameters
 * without a body class:
 *
 * <pre>{@code
 * transport.post(path, Params.of().set("role", role).map(), ref);
 * }</pre>
 */
public final class Params {

    private final Map<String, Object> map = new LinkedHashMap<>();

    private Params() {
    }

    public static Params of() {
        return new Params();
    }

    /** One-shot factory for a single key/value pair. */
    public static Params of(String key, Object value) {
        return of().set(key, value);
    }

    /** Set a key, or drop it when {@code value} is {@code null}. Returns {@code this}. */
    public Params set(String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
        return this;
    }

    /** The accumulated map (never {@code null}). */
    public Map<String, Object> map() {
        return map;
    }

    /** {@code true} when nothing was set — lets a caller send no body at all. */
    public boolean isEmpty() {
        return map.isEmpty();
    }
}
