package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * A custom domain (FAPI or accounts) for the instance.
 *
 * <p>The trailing {@code instructions} and {@code verified} fields are only
 * populated on the create and verify responses respectively (they are absent —
 * and so {@code null} — on list/get); they mirror the TypeScript intersection
 * return types.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomDomain(
        String object,
        String id,
        /** One of {@code fapi}, {@code accounts}. */
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
        String cookieDomain,
        /** Present only on the create response. */
        Map<String, Object> instructions,
        /** Present only on the verify response. */
        Boolean verified,
        /** DNS automation, or null. */ CustomDomainDns dns) {

    /** The previous shape, without {@code dns}. */
    public CustomDomain(String object, String id, String role, String host, String status, String action, boolean live, String cnameTarget, Long lastCheckedAt, String lastObservedTarget, String failureReason, Long certificateExpiresAt, String cookieDomain, Map<String, Object> instructions, Boolean verified) {
        this(object, id, role, host, status, action, live, cnameTarget, lastCheckedAt, lastObservedTarget, failureReason, certificateExpiresAt, cookieDomain, instructions, verified, null);
    }
}
