package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * An immutable, versioned OpenFGA authorization model. {@code model} is present
 * on get/create and omitted from list rows.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaAuthorizationModel(
        String object,
        String id,
        String storeId,
        String schemaVersion,
        Map<String, Object> model,
        long createdAt) {
}
