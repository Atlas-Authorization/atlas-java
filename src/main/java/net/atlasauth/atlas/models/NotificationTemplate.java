package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificationTemplate(
        String object,
        String id,
        String name,
        String subject,
        String body,
        String category,
        long createdAt,
        long updatedAt) {
}
