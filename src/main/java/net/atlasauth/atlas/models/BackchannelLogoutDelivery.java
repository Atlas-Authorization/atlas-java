package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** One back-channel logout attempt. Atlas never follows redirects: a 3xx fails with error {@code backchannel_redirect_not_followed} and its {@code location}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BackchannelLogoutDelivery(
        String object,
        String id,
        String oauthClientId,
        /** The ended session ({@code sid_test_…} for a test); null for "sign out everywhere". */ String sid,
        /** Sent by the test call; never retried. */ boolean test,
        /** pending, success or failed. */ String status,
        int attemptNumber,
        int maxAttempts,
        Integer httpStatus,
        Integer latencyMs,
        String error,
        String location,
        String responseSnippet,
        Long nextRetryAt,
        Long attemptedAt,
        Long deliveredAt,
        long createdAt) {
}
