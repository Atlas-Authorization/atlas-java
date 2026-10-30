package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** One entry of the app's own event catalog ({@code /v1/app_event_types}). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AppEventType(
        /** {@code app_event_type}. */ String object,
        /** {@code aet_} id. */ String id,
        /** Lowercase dotted name, e.g. {@code call.completed}. */ String type,
        String description,
        /** JSON Schema every published {@code data} must match; null = any object. */ Map<String, Object> schema,
        /** Still publishable; hidden from the endpoint picker. */ boolean deprecated,
        long createdAt,
        long updatedAt) {
}
