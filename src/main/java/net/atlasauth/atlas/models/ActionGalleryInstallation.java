package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** An action installed from the gallery, with its update status. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActionGalleryInstallation(
        String object,
        String actionId,
        String actionName,
        boolean enabled,
        String trigger,
        String source,
        String templateSlug,
        String installedVersion,
        String latestVersion,
        boolean updateAvailable,
        List<String> missingSecrets,
        List<String> updateRequiresSecrets,
        long installedAt,
        long updatedAt) {
}
