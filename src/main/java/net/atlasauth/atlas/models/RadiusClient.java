package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A RADIUS NAS client — a VPN concentrator, WiFi controller, or firewall allowed
 * to authenticate this instance's users against the shared Atlas RADIUS listener.
 *
 * <p>The per-NAS shared secret is WRITE-ONLY: it is sent on create/update but no
 * read ever returns it — a view reports only {@code has_secret}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RadiusClient(
        String object,
        String id,
        String name,
        String nasIdentifier,
        String ipAddress,
        /** Whether a shared secret is stored — never the secret itself. */
        boolean hasSecret,
        /** Blast-RADIUS (CVE-2024-3596) hardening: require attr 80 on Access-Requests. */
        boolean requireMessageAuthenticator,
        boolean enabled,
        long createdAt,
        long updatedAt) {
}
