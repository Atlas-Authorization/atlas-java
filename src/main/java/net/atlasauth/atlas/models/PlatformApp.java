package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** An app (instance) a platform owns. Never carries a secret except on create/rotate/claim. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformApp(
        String object,
        String id,
        String name,
        String environment,
        String publishableKey,
        List<String> allowedOrigins,
        List<String> tags,
        boolean archived,
        Long archivedAt,
        boolean disabled,
        long createdAt,
        long updatedAt) {
}
