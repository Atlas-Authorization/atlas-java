package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.MigrationsDeleteBridgeResponse;
import net.atlasauth.atlas.models.MigrationsExchangeBody;
import net.atlasauth.atlas.models.MigrationsExchangeResponse;
import net.atlasauth.atlas.models.MigrationsGetBridgeResponse;
import net.atlasauth.atlas.models.MigrationsSetBridgeBody;
import net.atlasauth.atlas.models.MigrationsSourcesResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/migrations} resource. */
public final class Migrations {

    private final Transport t;

    public Migrations(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/migrations/{source}/exchange} - Server-to-server exchange of an old `source` session for an Atlas session, once (what migrationBridge(&#123; source &#125;) calls with the secret key). The token is verified (vendor JWT against the bridge's issuer and keys, or sha256 matched against imported sessions), mapped to the imported user, spent in the single-use ledger and then takes the normal sign-in path (guards, MFA policy, post_login Actions, audit). Returns tokens in the body, or a sign_in_attempt when a second factor is owed. Limited per instance (6000/min), per end-user IP (60/min) and per user (10/min). Send the end user's IP / user agent in X-Atlas-Client-IP / X-Atlas-Client-User-Agent. Scope: {@code legacy_auth:write}. */
    public MigrationsExchangeResponse exchange(String source, MigrationsExchangeBody body) {
        return exchange(source, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public MigrationsExchangeResponse exchange(String source, MigrationsExchangeBody body, String idempotencyKey) {
        return t.request("POST", "/v1/migrations/" + enc(source) + "/exchange", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/migrations/sources} - Every migration source: what it exports, whether passwords move (or need lazy migration), its bridge preset and variables, and its lazy-migration preset. Scope: {@code jobs:read}. */
    public MigrationsSourcesResponse sources() {
        return sources(null, null);
    }

    /** As above, with every optional parameter. */
    public MigrationsSourcesResponse sources(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/migrations/sources", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/migrations/{source}/bridge} - Read the zero-logout bridge settings for `source` (window, JWT preset / issuer / keys, claims, cookie names) and exchange counts. Never returns a shared secret. Scope: {@code legacy_auth:read}. */
    public MigrationsGetBridgeResponse getBridge(String source) {
        return t.request("GET", "/v1/migrations/" + enc(source) + "/bridge", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/migrations/{source}/bridge} - Create or update the bridge settings for `source`. Every field is optional and merged onto the stored ones; a preset fills the JWT settings from its variables, explicit fields override it. The keys URL must be public https on a production instance. `better_auth` takes the Better Auth bridge body (see /v1/migrations/better_auth/bridge). Scope: {@code legacy_auth:write}. */
    public MigrationsGetBridgeResponse setBridge(String source, MigrationsSetBridgeBody body) {
        return t.request("PUT", "/v1/migrations/" + enc(source) + "/bridge", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/migrations/{source}/bridge} - Remove the bridge settings for `source` (the bridge is off and any stored secret deleted). Scope: {@code legacy_auth:write}. */
    public MigrationsDeleteBridgeResponse deleteBridge(String source) {
        return t.request("DELETE", "/v1/migrations/" + enc(source) + "/bridge", null, null, null, false, new TypeReference<>() {
        });
    }

    /** The {@code imports} sub-resource. */
    public MigrationsImports imports() {
        return new MigrationsImports(t);
    }
}
