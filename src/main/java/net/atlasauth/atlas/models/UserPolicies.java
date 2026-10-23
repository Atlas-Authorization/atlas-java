package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** {@code GET /v1/users/:id/policies}: a user's standing on every policy. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserPolicies(
        String object,
        String userId,
        List<PolicyStanding> data,
        Boolean needsAcceptance) {
}
