package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformAppDisabled(
        String object,
        String id,
        boolean disabled,
        String note) {
}
