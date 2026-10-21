package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The trusted reverse proxy secret's state, never the secret. A proxy that serves the Frontend
 * API from your own origin sends the secret in {@code X-Atlas-Proxy-Secret} and the browser's IP
 * in {@code X-Atlas-Client-IP}; Atlas then uses that IP for rate limits, lockout, risk signals,
 * geo, audit logs and session metadata.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProxySecret(
        String object,
        /** Whether a proxy secret is set. */ boolean set,
        /** When the current secret was created (epoch ms), or null. */ Long createdAt,
        /** After a rotation: when the replaced secret stops working (epoch ms), or null. */ Long previousExpiresAt,
        /** {@code X-Atlas-Proxy-Secret}. */ String secretHeader,
        /** {@code X-Atlas-Client-IP}. */ String clientIpHeader) {
}
