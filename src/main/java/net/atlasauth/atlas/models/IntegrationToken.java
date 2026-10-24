package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * An identity-integration token (a short-lived JWT for a service such as chatly or forumulate); its claims
 * follow {@code https://atlasauth.net/schemas/suite-token-v1.json}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IntegrationToken(
        String object,
        String integration,
        String product,
        String jwt,
        /** Seconds until the token expires (at most 120). */ int expiresIn,
        String audience,
        String orgId,
        String spaceId,
        String redeemUrl,
        String issuer,
        String jwksUri) {
}
