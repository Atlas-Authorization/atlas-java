package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeletedNotificationCategory(
        String object,
        String key,
        Boolean deleted) {
}
