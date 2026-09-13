package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrgDomain(
        String object,
        String id,
        String organizationId,
        String domain,
        String status,
        boolean autoJoin,
        String defaultRoleId,
        OrgDomainVerification verification,
        Long verifiedAt,
        Long lastCheckedAt,
        long createdAt) {
}
