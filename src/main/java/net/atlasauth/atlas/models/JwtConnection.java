package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A trusted-JWT connection: your system signs a short-lived JWT and sends the person to {@code loginUrl}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JwtConnection(
        String object,
        String id,
        String organizationId,
        /** draft, active or disabled. */
        String status,
        String jwksUri,
        String issuer,
        String audience,
        String emailClaim,
        int maxAgeSeconds,
        String defaultRoleId,
        /** Send the person here with {@code ?token=<jwt>&redirect_url=<your app URL>}. */
        String loginUrl,
        long createdAt,
        long updatedAt) {
}
