package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Tenant code run in the hardened isolate at a trigger. Secret VALUES are
 * write-only and never returned — a read reports only {@code secret_names}.
 * Timestamps are epoch milliseconds.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Action(
        String object,
        String id,
        String name,
        String trigger,
        String code,
        String runtime,
        boolean enabled,
        List<String> secretNames,
        long createdAt,
        long updatedAt) {
}
