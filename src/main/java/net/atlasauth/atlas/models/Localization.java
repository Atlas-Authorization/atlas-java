package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * A per-locale override the render paths resolve at send/render time. Always an
 * OVERRIDE: the base renders whenever no row matches the target locale.
 * Timestamps are ISO strings, not epoch numbers.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Localization(
        String object,
        String id,
        String resourceType,
        String resourceName,
        String locale,
        Map<String, Object> content,
        boolean enabled,
        String createdAt,
        String updatedAt) {
}
