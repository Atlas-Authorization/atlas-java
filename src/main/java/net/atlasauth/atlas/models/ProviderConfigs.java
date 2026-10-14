package net.atlasauth.atlas.models;

import java.util.LinkedHashMap;
import java.util.Map;

/** Builds the exact {@code config} map for a provider, omitting unset (null) keys. */
final class ProviderConfigs {

    private ProviderConfigs() {
    }

    static Map<String, Object> of(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            if (kv[i + 1] != null) {
                m.put((String) kv[i], kv[i + 1]);
            }
        }
        return m;
    }
}
