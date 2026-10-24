package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;

import static net.atlasauth.atlas.http.Transport.enc;

/** {@code /v1/atlas_id_apps} — Sign in with Atlas grants for the apps this instance manages (privacy requests: find a person, revoke their grant). */
public final class AtlasIdGrants {
    private final Transport t;

    public AtlasIdGrants(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/atlas_id_apps} — The Sign in with Atlas apps this instance manages. */
    public JsonNode listApps() {
        return t.get("/v1/atlas_id_apps", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/atlas_id_apps/{id}/users?email=} — Is this verified email a user of the app? An empty list when not. */
    public JsonNode lookup(String appId, String email) {
        return t.get("/v1/atlas_id_apps/" + enc(appId) + "/users", Params.of("email", email).map(), new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/atlas_id_apps/{id}/grants/{grantId}} — Revoke one user's grant (needs {@code privacy:grants:revoke} or the per-app grant scope). */
    public JsonNode revoke(String appId, String grantId) {
        return t.delete("/v1/atlas_id_apps/" + enc(appId) + "/grants/" + enc(grantId), new TypeReference<>() {
        });
    }
}
