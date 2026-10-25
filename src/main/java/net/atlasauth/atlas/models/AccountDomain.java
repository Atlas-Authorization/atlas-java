package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A domain the account claimed; verified ones gate test sign-in mode and the test-account domain restriction. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountDomain(
        String object,
        String id,
        String domain,
        boolean verified,
        Long verifiedAt,
        Long lastCheckedAt,
        AccountDomainVerification verification,
        Boolean recordFound,
        long createdAt) {
}
