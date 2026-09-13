package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Managed AWS WAF config + provisioning state. Secrets are write-only and never
 * returned; only {@code has_*} markers are exposed. When the instance has no
 * config the response collapses to {@code configured=false, enabled=false,
 * status="disabled"} and the config fields are absent.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ManagedWaf(
        String object,
        boolean configured,
        boolean enabled,
        String status,
        String scope,
        String region,
        String webAclName,
        String edgeResourceArn,
        String cloudfrontDistributionId,
        List<String> tokenDomains,
        List<String> gatedPaths,
        Integer immunitySeconds,
        ManagedWafCredential credential,
        String webAclId,
        String webAclArn,
        String lastError,
        Long lastProvisionedAt,
        Long updatedAt) {
}
