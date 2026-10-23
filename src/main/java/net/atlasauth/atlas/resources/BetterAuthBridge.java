package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.BetterAuthBridgeSettings;
import net.atlasauth.atlas.models.BetterAuthExchangeResponse;
import net.atlasauth.atlas.models.DeletedBetterAuthBridge;
import net.atlasauth.atlas.models.UpdateBetterAuthBridgeBody;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Better Auth session bridge ({@code /v1/migrations/better_auth/*}): during a migration window a
 * returning user's Better Auth session is exchanged, once, for an Atlas session, so nobody is signed out by
 * the switch. See {@link net.atlasauth.atlas.betterauth.BetterAuth#exchange} for a classified result and
 * {@link net.atlasauth.atlas.betterauth.SessionBridge} for the request-level bridge.
 */
public final class BetterAuthBridge {

    /** The Backend API path of the secret-key exchange. */
    public static final String EXCHANGE_PATH = "/v1/migrations/better_auth/exchange";

    private final Transport t;

    public BetterAuthBridge(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/migrations/better_auth/bridge} — the settings and exchange counts (never the secret). */
    public BetterAuthBridgeSettings get() {
        return t.get("/v1/migrations/better_auth/bridge", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/migrations/better_auth/bridge} — create or update the settings (merged onto the stored ones). */
    public BetterAuthBridgeSettings update(UpdateBetterAuthBridgeBody body) {
        return update(body, null);
    }

    public BetterAuthBridgeSettings update(UpdateBetterAuthBridgeBody body, String idempotencyKey) {
        return t.request("PUT", "/v1/migrations/better_auth/bridge", null,
                body != null ? body : new UpdateBetterAuthBridgeBody(), idempotencyKey, false,
                new TypeReference<>() {
                });
    }

    /** {@code DELETE /v1/migrations/better_auth/bridge} — the bridge is off and the stored secret is deleted. */
    public DeletedBetterAuthBridge delete() {
        return t.delete("/v1/migrations/better_auth/bridge", new TypeReference<>() {
        });
    }

    /** {@link #exchange(String, String, String, Boolean)} without forwarding the end user. */
    public BetterAuthExchangeResponse exchange(String token) {
        return exchange(token, null, null, null);
    }

    /**
     * {@code POST /v1/migrations/better_auth/exchange} — exchange a Better Auth session (the raw cookie value
     * or the bare token) for an Atlas session, once. {@code clientIp} / {@code userAgent} are the END USER's,
     * forwarded ({@code X-Atlas-Client-IP}, {@code X-Atlas-Client-User-Agent}) so Atlas's guards, risk and geo
     * checks and the device record see the user rather than your server. {@code remember} {@code false}
     * records a browser-session session. Throws {@link net.atlasauth.atlas.AtlasApiException} on a refusal
     * ({@code meta.bridge_outcome}).
     */
    public BetterAuthExchangeResponse exchange(String token, String clientIp, String userAgent, Boolean remember) {
        Map<String, String> headers = new LinkedHashMap<>();
        if (clientIp != null && !clientIp.isEmpty()) {
            headers.put("X-Atlas-Client-IP", clientIp);
        }
        if (userAgent != null && !userAgent.isEmpty()) {
            headers.put("X-Atlas-Client-User-Agent", userAgent);
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("token", token);
        if (Boolean.FALSE.equals(remember)) {
            body.put("remember", false);
        }
        return t.requestWithHeaders("POST", EXCHANGE_PATH, null, body, null, headers, new TypeReference<>() {
        });
    }
}
