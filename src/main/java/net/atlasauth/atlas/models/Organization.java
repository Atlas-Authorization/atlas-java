package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Organization(
        String object,
        String id,
        String name,
        String slug,
        String imageUrl,
        Map<String, Object> publicMetadata,
        int maxAllowedMemberships,
        /** Null when your backend created the organization without a creator. */
        String createdBy,
        long createdAt,
        long updatedAt,
        /** Your own id for this customer; unique per instance. */
        String externalId,
        /** Role key to your product's name for it (echoed as the {@code org_role_mapped} token claim). */
        Map<String, String> roleMappings,
        /** On create with {@code owner_email} only: the invitation sent to that address. */
        OwnerInvitation ownerInvitation,
        /** {@code GET /v1/organizations/{id}} only: the organization's enterprise sign-in setup. */
        Sso sso,
        /** {@code GET /v1/organizations/by_domain/{domain}} only: the matched domain. */
        MatchedDomain domain) {

    /** The 1.19 shape, without externalId, roleMappings, ownerInvitation, sso and domain. */
    public Organization(String object, String id, String name, String slug, String imageUrl, Map<String, Object> publicMetadata, int maxAllowedMemberships, String createdBy, long createdAt, long updatedAt) {
        this(object, id, name, slug, imageUrl, publicMetadata, maxAllowedMemberships, createdBy, createdAt, updatedAt, null, null, null, null, null);
    }

    /** The invitation sent to {@code owner_email} on create. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OwnerInvitation(String object, String id, String email, String role, String status, /** Epoch ms. */ Long expiresAt) {
    }

    /** The organization's SSO connections and the SCIM 2.0 base URL to give its identity provider. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sso(List<SsoConnectionSummary> connections, String scimBaseUrl) {
    }

    /** One SSO connection of the organization; status is draft, active or disabled. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SsoConnectionSummary(String id, String type, String status) {
    }

    /** The domain a by-domain lookup matched; status is pending, verified or failed. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MatchedDomain(String object, String id, String domain, String status, Long verifiedAt) {
    }
}
