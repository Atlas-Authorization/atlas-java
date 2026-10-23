package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CustomOAuthProvider;
import net.atlasauth.atlas.models.CustomOAuthProviderBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.ProfileMappingPreview;
import net.atlasauth.atlas.models.UpstreamSigningKey;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * {@code /v1/custom_oauth_providers} — your own upstream OAuth 2.0 / OpenID Connect login
 * providers, with Atlas as the relying party: endpoints (or just an OIDC issuer),
 * token-endpoint client authentication (incl. private_key_jwt with an Atlas-held key), a
 * declarative profile mapping, upstream logout and third-party initiated login. The
 * provider then signs users in like any built-in one, under its {@code providerKey}.
 */
public final class CustomOAuthProviders {

    private final Transport t;

    public CustomOAuthProviders(Transport t) {
        this.t = t;
    }

    public ListPage<CustomOAuthProvider> list() {
        return t.get("/v1/custom_oauth_providers", new TypeReference<>() {
        });
    }

    public CustomOAuthProvider get(String id) {
        return t.get("/v1/custom_oauth_providers/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code providerKey} and {@code displayName} are required. */
    public CustomOAuthProvider create(CustomOAuthProviderBody body) {
        return create(body, null);
    }

    public CustomOAuthProvider create(CustomOAuthProviderBody body, String idempotencyKey) {
        return t.post("/v1/custom_oauth_providers", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** Patch a provider; {@code body.set(key, null)} clears a nullable setting. */
    public CustomOAuthProvider update(String id, CustomOAuthProviderBody body) {
        return t.patch("/v1/custom_oauth_providers/" + enc(id), body, new TypeReference<>() {
        });
    }

    public JsonNode delete(String id) {
        return t.delete("/v1/custom_oauth_providers/" + enc(id), new TypeReference<>() {
        });
    }

    /** Fetch and validate an issuer's OpenID configuration (for autofill); {@code discoveryUrl} may be null. */
    public JsonNode discover(String issuer, String discoveryUrl) {
        return t.post("/v1/custom_oauth_providers/discover",
                Params.of().set("issuer", issuer).set("discovery_url", discoveryUrl).map(), new TypeReference<>() {
                });
    }

    /**
     * Run a mapping ({@code profile_mapping}) against a pasted sample ({@code userinfo},
     * {@code id_token_claims}, {@code id_token}).
     */
    public ProfileMappingPreview previewProfileMapping(Map<String, Object> body) {
        return t.post("/v1/custom_oauth_providers/profile_mapping/preview", body, new TypeReference<>() {
        });
    }

    /** Run a provider's stored mapping (or the body's) against a pasted sample. */
    public ProfileMappingPreview previewProfileMapping(String id, Map<String, Object> body) {
        return t.post("/v1/custom_oauth_providers/" + enc(id) + "/profile_mapping/preview", body,
                new TypeReference<>() {
                });
    }

    /** The provider's private_key_jwt signing keys. */
    public ListPage<UpstreamSigningKey> listSigningKeys(String id) {
        return t.get("/v1/custom_oauth_providers/" + enc(id) + "/signing_keys", new TypeReference<>() {
        });
    }

    /**
     * Generate a new active key ({@code RS256}, {@code PS256} or {@code ES256}; null for the
     * default); the previous one turns {@code retiring} (still published).
     */
    public UpstreamSigningKey rotateSigningKey(String id, String alg) {
        return t.post("/v1/custom_oauth_providers/" + enc(id) + "/signing_keys", Params.of().set("alg", alg).map(),
                new TypeReference<>() {
                });
    }

    /** Retire a non-active key (it leaves the JWKS). */
    public UpstreamSigningKey retireSigningKey(String id, String kid) {
        return t.post("/v1/custom_oauth_providers/" + enc(id) + "/signing_keys/" + enc(kid) + "/retire", null,
                new TypeReference<>() {
                });
    }
}
