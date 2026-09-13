package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * The outcome of a provision/deprovision: the full {@link ManagedWaf} config plus
 * the fail-safe result. An AWS failure rides {@code ok=false} + {@code error} in
 * the body rather than throwing, so it never looks like an Atlas outage.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ManagedWafOutcome(
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
        Long updatedAt,
        boolean ok,
        String error) {
}
