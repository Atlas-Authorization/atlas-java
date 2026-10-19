package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One logout_token sent to a fan-out endpoint. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LogoutNotifyDelivery(
        String object,
        String id,
        String sub,
        /** null = a subject-wide logout (ban / deletion). */ String sid,
        /** pending, success or failed. */ String status,
        int attemptNumber,
        Integer httpStatus,
        String responseSnippet,
        Long nextRetryAt,
        Long deliveredAt,
        long createdAt) {
}
