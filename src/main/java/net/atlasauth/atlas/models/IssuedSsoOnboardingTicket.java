package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The create response: the ticket plus the once-only token and hosted URL.
 * Mirrors {@link SsoOnboardingTicket} with the three reveal-once fields added.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IssuedSsoOnboardingTicket(
        String object,
        String id,
        String profileId,
        String organizationId,
        String ssoConnectionId,
        String status,
        long expiresAt,
        long createdAt,
        Long completedAt,
        /** Revealed exactly once; only its hash is stored. */
        String token,
        /** The hosted URL the end-customer's IT admin opens (no dashboard account). */
        String url,
        long expiresIn) {
}
