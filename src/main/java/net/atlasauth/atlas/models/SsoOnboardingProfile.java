package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A self-service SSO onboarding profile — the WorkOS Admin Portal / Auth0
 * self-service-profiles model. It fixes which protocols an end-customer may
 * configure and either binds an org or promises to name one at ticket issue.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SsoOnboardingProfile(
        String object,
        String id,
        String name,
        List<String> allowedConnectionTypes,
        /** Set = every ticket targets this org; null = the ticket names its org. */
        String organizationId,
        String companyName,
        boolean allowScim,
        long createdAt,
        long updatedAt) {
}
