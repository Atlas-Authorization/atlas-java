package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A per-instance IP allow/deny rule. {@code action} is {@code "allow"} or
 * {@code "deny"}; rules are evaluated in priority order (lower first), first
 * match wins.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NetworkAcl(
        String object,
        String id,
        String action,
        String cidr,
        String description,
        int priority,
        boolean enabled,
        long createdAt,
        long updatedAt) {
}
