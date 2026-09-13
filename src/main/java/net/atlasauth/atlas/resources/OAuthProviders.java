package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.OAuthConnectionTest;
import net.atlasauth.atlas.models.OAuthProvider;
import net.atlasauth.atlas.models.UpsertOAuthProviderBody;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/oauth_providers} resource — social sign-in provider catalog + config. */
public final class OAuthProviders {

    private final Transport t;

    public OAuthProviders(Transport t) {
        this.t = t;
    }

    /** The whole catalog, configured or not. */
    public ListPage<OAuthProvider> list() {
        return t.get("/v1/oauth_providers", new TypeReference<>() {
        });
    }

    public OAuthProvider get(String provider) {
        return t.get("/v1/oauth_providers/" + enc(provider), new TypeReference<>() {
        });
    }

    public JsonNode upsert(String provider, UpsertOAuthProviderBody body) {
        return upsert(provider, body, null);
    }

    /** Set (or replace) this instance's own credentials for a provider. */
    public JsonNode upsert(String provider, UpsertOAuthProviderBody body, String idempotencyKey) {
        // PUT with an idempotency key — no convenience verb exposes that, so call request() directly.
        return t.request("PUT", "/v1/oauth_providers/" + enc(provider), null, body, idempotencyKey, false,
                new TypeReference<>() {
                });
    }

    /** Returns {@code {object,provider,deleted,note}}. */
    public JsonNode delete(String provider) {
        return t.delete("/v1/oauth_providers/" + enc(provider), new TypeReference<>() {
        });
    }

    /** Turn a configured provider on or off. Refused if it has no usable credentials. */
    public JsonNode setEnabled(String provider, boolean enabled) {
        Map<String, Object> body = Params.of("enabled", enabled).map();
        return t.post("/v1/oauth_providers/" + enc(provider) + "/enabled", body, new TypeReference<>() {
        });
    }

    /** §6.5 which screens this provider serves. At least one must be true. */
    public JsonNode setScope(String provider, boolean allowSignIn, boolean allowSignUp) {
        Map<String, Object> body = Params.of()
                .set("allow_sign_in", allowSignIn)
                .set("allow_sign_up", allowSignUp)
                .map();
        return t.post("/v1/oauth_providers/" + enc(provider) + "/scope", body, new TypeReference<>() {
        });
    }

    /** Verify the stored credentials against the provider's token endpoint, without enabling it. */
    public OAuthConnectionTest test(String provider) {
        return t.post("/v1/oauth_providers/" + enc(provider) + "/test", null, new TypeReference<>() {
        });
    }
}
