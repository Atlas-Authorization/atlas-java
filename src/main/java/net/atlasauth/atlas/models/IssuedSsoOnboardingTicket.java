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
        long expiresIn,
        /** This ticket's return_url (null = the profile's applies). */
        String returnUrl,
        /** Where "Finish" will send the admin: the ticket's return_url, else the profile's. */
        String effectiveReturnUrl) {

    /** The 1.19 shape, without returnUrl and effectiveReturnUrl. */
    public IssuedSsoOnboardingTicket(String object, String id, String profileId, String organizationId, String ssoConnectionId, String status, long expiresAt, long createdAt, Long completedAt, String token, String url, long expiresIn) {
        this(object, id, profileId, organizationId, ssoConnectionId, status, expiresAt, createdAt, completedAt, token, url, expiresIn, null, null);
    }
}
