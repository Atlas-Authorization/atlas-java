package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The account's Sign in with Atlas publisher (name and DNS-verified domain). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdPublisher(
        String object,
        String name,
        String domain,
        boolean verified,
        Long verifiedAt,
        /** Publisher-wide consent for verified apps (openid / email / profile only). */ boolean skipConsent,
        AtlasIdPublisherVerification verification,
        Long lastCheckedAt,
        Long createdAt,
        Long updatedAt,
        /** Set by verify: whether the TXT record matched. */ Boolean recordFound) {
}
