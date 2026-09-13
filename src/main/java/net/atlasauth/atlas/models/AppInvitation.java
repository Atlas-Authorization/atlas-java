package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * A top-level (application) invitation from {@code /v1/invitations}, distinct
 * from an {@link OrganizationInvitation}. {@code acceptedAt} is null until the
 * invitation is accepted.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AppInvitation(
        String object,
        String id,
        String emailAddress,
        String status,
        Map<String, Object> publicMetadata,
        long expiresAt,
        Long acceptedAt,
        long createdAt) {
}
