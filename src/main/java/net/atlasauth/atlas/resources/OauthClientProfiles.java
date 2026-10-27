package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.OauthClientProfilesDeleteResponse;
import net.atlasauth.atlas.models.OauthClientProfilesListResponse;
import net.atlasauth.atlas.models.OauthClientProfilesListResponseDataItem;
import net.atlasauth.atlas.models.OauthClientProfilesSetBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/oauth_client_profiles} resource. */
public final class OauthClientProfiles {

    private final Transport t;

    public OauthClientProfiles(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/oauth_client_profiles} - List the OAuth client policy profiles: the built-ins (fapi2_security, fapi2_message_signing, pkce_required, par_required, dpop_required) and your custom ones. Scope: {@code oauth_clients:read}. */
    public OauthClientProfilesListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public OauthClientProfilesListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/oauth_client_profiles", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/oauth_client_profiles/{name}} - Create or replace a custom client policy profile (rules: PKCE, PAR, sender constraint, signed request object, allowed auth methods and signing algorithms, https-only redirects, allowed claims). Built-in names are reserved. Scope: {@code oauth_clients:write}. */
    public OauthClientProfilesListResponseDataItem set(String name, OauthClientProfilesSetBody body) {
        return t.request("PUT", "/v1/oauth_client_profiles/" + enc(name), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/oauth_client_profiles/{name}} - Delete a custom client policy profile. 409 while any client is assigned it. Scope: {@code oauth_clients:write}. */
    public OauthClientProfilesDeleteResponse delete(String name) {
        return t.request("DELETE", "/v1/oauth_client_profiles/" + enc(name), null, null, null, false, new TypeReference<>() {
        });
    }
}
