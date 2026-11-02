package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrgDomain(
        String object,
        String id,
        String organizationId,
        String domain,
        /** pending, verified or failed. */
        String status,
        boolean autoJoin,
        String defaultRoleId,
        OrgDomainVerification verification,
        Long verifiedAt,
        Long lastCheckedAt,
        long createdAt,
        /** Every way to prove the domain. */
        VerificationMethods verificationMethods,
        /** How the domain was verified ({@code dns_txt}, {@code well_known}, {@code email}); null while unverified. */
        String verificationMethod,
        /** {@code verification_email} only: the address the code went to. */
        String sentTo) {

    /** The 1.19 shape, without verificationMethods, verificationMethod and sentTo. */
    public OrgDomain(String object, String id, String organizationId, String domain, String status, boolean autoJoin, String defaultRoleId, OrgDomainVerification verification, Long verifiedAt, Long lastCheckedAt, long createdAt) {
        this(object, id, organizationId, domain, status, autoJoin, defaultRoleId, verification, verifiedAt, lastCheckedAt, createdAt, null, null, null);
    }

    /** The DNS TXT record, the well-known file and the emailed code. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VerificationMethods(OrgDomainVerification dnsTxt, WellKnown wellKnown, Email email) {
    }

    /** No DNS access: serve {@code body} as the whole response (200, {@code contentType}) at {@code url}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WellKnown(String url, String body, String contentType) {
    }

    /** A code mailed to one of {@code addresses}; {@code sentTo} and {@code expiresAt} (epoch ms) once sent. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Email(List<String> addresses, String sentTo, Long expiresAt) {
    }
}
