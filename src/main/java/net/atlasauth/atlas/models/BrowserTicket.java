package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A one-time browser sign-in ticket ({@code POST /v1/sessions} with {@code mode: "browser_ticket"}).
 * Redeem {@code ticket} with {@code POST /v1/client/sign_ins {strategy:"ticket"}}, or open {@code url}
 * in the browser. No session is minted until the browser redeems it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BrowserTicket(
        /** {@code browser_ticket}. */ String object,
        String id,
        String userId,
        String ticket,
        /** The ready sign-in URL; null unless a {@code redirectUrl} was given. */ String url,
        String redirectUrl,
        /** Lifetime in seconds. */ long expiresIn,
        /** ISO 8601. */ String expiresAt) {
}
