package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A one-time, expiring ticket for a specific end-customer org. Its opaque token
 * (and the hosted setup URL carrying it) are revealed exactly once — see
 * {@link IssuedSsoOnboardingTicket}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SsoOnboardingTicket(
        String object,
        String id,
        String profileId,
        String organizationId,
        String ssoConnectionId,
        String status,
        long expiresAt,
        long createdAt,
        Long completedAt) {
}
