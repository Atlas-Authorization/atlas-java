package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A GDPR/DSAR data-subject request. A fulfilled or rejected request is terminal —
 * a second action is a 409.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DataSubjectRequest(
        String object,
        String id,
        String type,
        String status,
        String userId,
        String requestedByType,
        String requestedById,
        String reason,
        Long scheduledFor,
        long requestedAt,
        long updatedAt,
        Long completedAt,
        /** The export package — present on {@code get} and on a fulfilled export. */
        Object result) {
}
