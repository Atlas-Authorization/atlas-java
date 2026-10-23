package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * The {@code /v1/oauth_initial_access_tokens} resource — tokens that gate OAuth dynamic client
 * registration (RFC 7591 §3) once {@code oauthProvider.requireInitialAccessToken} is on.
 */
public final class OAuthInitialAccessTokens {

    private final Transport t;

    public OAuthInitialAccessTokens(Transport t) {
        this.t = t;
    }

    public JsonNode list() {
        return t.get("/v1/oauth_initial_access_tokens", new TypeReference<>() {
        });
    }

    /** Mint a token (all arguments nullable); {@code token} is returned exactly once. */
    public JsonNode create(String description, Long expiresInSeconds, Integer maxUses) {
        return t.post("/v1/oauth_initial_access_tokens",
                Params.of("description", description).set("expires_in_seconds", expiresInSeconds)
                        .set("max_uses", maxUses).map(),
                new TypeReference<>() {
                });
    }

    public JsonNode revoke(String id) {
        return t.delete("/v1/oauth_initial_access_tokens/" + enc(id), new TypeReference<>() {
        });
    }
}
