package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A reader-pool custom domain (its `object` const is 'custom_domain'). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReaderDomain(
        String object,
        String id,
        String role,
        String host,
        String status,
        String action,
        boolean live,
        String cnameTarget,
        Long lastCheckedAt,
        String lastObservedTarget,
        String failureReason,
        Long certificateExpiresAt,
        String cookieDomain) {
}
