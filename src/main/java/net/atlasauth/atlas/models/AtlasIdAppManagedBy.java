package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@code PUT /v1/platform/atlas_id_apps/{id}/managed_by}: the app after the change (the identifying fields; fetch the app
 * for the rest) and whether anything changed.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdAppManagedBy(
        String object,
        String id,
        String clientId,
        String name,
        String managedByInstanceId,
        /** instance_toggle or adopted; null once taken back. */ String managedVia,
        Long updatedAt,
        boolean changed) {
}
