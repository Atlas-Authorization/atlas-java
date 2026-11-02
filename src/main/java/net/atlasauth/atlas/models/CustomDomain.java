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
        /** Null for a path mount (no CNAME). */
        String cnameTarget,
        Long lastCheckedAt,
        String lastObservedTarget,
        String failureReason,
        Long certificateExpiresAt,
        String cookieDomain,
        /** The create response; a path mount, once verified: nothing to add, or the reverse-proxy rule. */
        Map<String, Object> instructions,
        /** Present only on the verify response. */
        Boolean verified,
        /** DNS automation, or null. */ CustomDomainDns dns,
        /** {@code cname} or {@code path} (a path mount on your own host, proven by an https file). */ String method,
        /** Path mount: the prefix the Frontend API is served under. */ String basePath,
        /** Path mount, once verified: {@code atlas_ingress} or {@code customer_proxy}. */ String routing,
        /** Path mount: {@code https://<host><base_path>}, the Frontend API and (once verified) the issuer. */ String frontendApiUrl,
        /** Path mount: publish {@code content} at {@code url} (status 200, no redirect), then verify. */ PathVerification verification,
        /** The verify response of a path mount, once verified: the issuer tokens now carry. */ String issuer) {

    /** The 1.19 shape, without the path-mount fields. */
    public CustomDomain(String object, String id, String role, String host, String status, String action, boolean live, String cnameTarget, Long lastCheckedAt, String lastObservedTarget, String failureReason, Long certificateExpiresAt, String cookieDomain, Map<String, Object> instructions, Boolean verified, CustomDomainDns dns) {
        this(object, id, role, host, status, action, live, cnameTarget, lastCheckedAt, lastObservedTarget, failureReason, certificateExpiresAt, cookieDomain, instructions, verified, dns, null, null, null, null, null, null);
    }

    /** The previous shape, without {@code dns}. */
    public CustomDomain(String object, String id, String role, String host, String status, String action, boolean live, String cnameTarget, Long lastCheckedAt, String lastObservedTarget, String failureReason, Long certificateExpiresAt, String cookieDomain, Map<String, Object> instructions, Boolean verified) {
        this(object, id, role, host, status, action, live, cnameTarget, lastCheckedAt, lastObservedTarget, failureReason, certificateExpiresAt, cookieDomain, instructions, verified, null);
    }

    /** The https file that proves a path-mount host: its whole body is {@code token}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PathVerification(String method, String url, String token, String content, String instructions) {
    }
}
