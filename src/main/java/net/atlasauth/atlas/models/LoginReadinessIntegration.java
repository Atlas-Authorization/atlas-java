package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The integration the login-readiness checks assumed for an origin, and why.
 *
 * @param assumed {@code redirect} (hosted-redirect OIDC: the {@code cors} and
 *                {@code session_cookie} checks do not apply) or {@code embedded}
 * @param source  {@code declared}, {@code embedded_traffic}, {@code redirect_clients} or {@code default}
 * @param reason  a plain sentence saying why
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginReadinessIntegration(String assumed, String source, String reason) {
}
