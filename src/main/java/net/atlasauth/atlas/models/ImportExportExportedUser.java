package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** A user as serialised into an export job's {@code result}. Never a secret. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ImportExportExportedUser(
        String object,
        String id,
        String username,
        String firstName,
        String lastName,
        String imageUrl,
        Map<String, Object> publicMetadata,
        Map<String, Object> privateMetadata,
        List<ImportExportEmailAddress> emailAddresses,
        boolean mfaEnabled,
        boolean banned,
        long createdAt,
        long updatedAt) {
}
