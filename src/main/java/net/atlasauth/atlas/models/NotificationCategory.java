package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificationCategory(
        String object,
        String id,
        String key,
        String label,
        boolean optional,
        long createdAt) {
}
