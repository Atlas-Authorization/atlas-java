package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificationPreference(
        String category,
        String label,
        boolean enabled,
        boolean optional) {
}
