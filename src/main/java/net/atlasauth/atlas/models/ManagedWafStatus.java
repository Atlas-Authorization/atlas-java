package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Just the provisioning state — what {@code GET /v1/managed_waf/status} returns. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ManagedWafStatus(
        String object,
        boolean enabled,
        String status,
        String webAclId,
        String webAclArn,
        String lastError,
        Long lastProvisionedAt) {
}
