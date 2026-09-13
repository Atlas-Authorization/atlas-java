package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** One immutable entry in the instance's audit trail. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuditLog(
        String object,
        String id,
        String actorType,
        String actorId,
        String action,
        String targetType,
        String targetId,
        Map<String, Object> metadata,
        long createdAt) {
}
