package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * An LTI 1.3 platform (LMS) registration for a core launch. Every field is a
 * PUBLIC trust anchor — a launch is verified against the platform's own
 * published JWKS, not a shared secret — so a registration is fully readable and
 * editable with no reveal-once.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LtiPlatform(
        String object,
        String id,
        String organizationId,
        String issuer,
        String clientId,
        String authLoginUrl,
        String jwksUri,
        List<String> deploymentIds,
        long createdAt,
        long updatedAt) {
}
