package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformAppBulkDeleteItem(
        String id,
        boolean deleted,
        String reason) {
}
