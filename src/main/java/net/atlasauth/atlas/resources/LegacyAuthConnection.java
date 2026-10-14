package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DeletedLegacyAuthConnection;
import net.atlasauth.atlas.models.LegacyAuthConnectionBody;
import net.atlasauth.atlas.models.LegacyAuthConnectionConfig;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The lazy-migration (legacy-auth / custom database) connection ({@code /v1/legacy_auth_connection}). */
public final class LegacyAuthConnection {

    private final Transport t;

    public LegacyAuthConnection(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/legacy_auth_connection} — Read the instance's lazy-migration (legacy-auth / custom-DB) connection. */
    public LegacyAuthConnectionConfig get() {
        return t.get("/v1/legacy_auth_connection", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/legacy_auth_connection} — Create or replace the instance's lazy-migration connection (verify_url + signing_secret, enabled, import_on_login, timeout_ms). */
    public LegacyAuthConnectionConfig replace(LegacyAuthConnectionBody body) {
        return t.put("/v1/legacy_auth_connection", body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/legacy_auth_connection} — Delete the instance's lazy-migration (legacy-auth / custom-DB) connection. */
    public DeletedLegacyAuthConnection delete() {
        return t.delete("/v1/legacy_auth_connection", new TypeReference<>() {
        });
    }
}
