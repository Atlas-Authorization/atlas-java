package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformAppWithSecret(
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
        long updatedAt,
        String secret,
        String note) {
}
