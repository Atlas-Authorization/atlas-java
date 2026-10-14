package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeletedNotificationTemplate(
        String object,
        String name,
        Boolean deleted) {
}
