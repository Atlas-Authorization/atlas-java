package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.OAuthClientsAppSettingsGetResponse;
import net.atlasauth.atlas.models.OAuthClientsAppSettingsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/oauth_clients} resource. */
public final class OAuthClientsAppSettings {

    private final Transport t;

    public OAuthClientsAppSettings(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/oauth_clients/{id}/app_settings} - The per-application settings of an OAuth client: allowed sign-in methods, hosted-page branding, the user-claim allow-list for ID tokens and userinfo, and the traits a session from this app may write. Null = no restriction. Scope: {@code oauth_clients:read}. */
    public OAuthClientsAppSettingsGetResponse get(String id) {
        return t.request("GET", "/v1/oauth_clients/" + enc(id) + "/app_settings", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/oauth_clients/{id}/app_settings} - Change the per-application settings. Each field sent replaces the stored one and null clears it; fields left out keep their value. Unknown fields and malformed values are refused. Scope: {@code oauth_clients:write}. */
    public OAuthClientsAppSettingsGetResponse update(String id, OAuthClientsAppSettingsUpdateBody body) {
        return t.request("PATCH", "/v1/oauth_clients/" + enc(id) + "/app_settings", null, body, null, false, new TypeReference<>() {
        });
    }
}
