package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.JwtConnection;
import net.atlasauth.atlas.models.JwtConnectionBody;
import net.atlasauth.atlas.models.ListPage;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/jwt_connections} resource: sign-in with a JWT your own system signs (trusted-JWT SSO). */
public final class JwtConnections {

    private final Transport t;

    public JwtConnections(Transport t) {
        this.t = t;
    }

    public ListPage<JwtConnection> list() {
        return t.get("/v1/jwt_connections", new TypeReference<>() {
        });
    }

    public JwtConnection get(String id) {
        return t.get("/v1/jwt_connections/" + enc(id), new TypeReference<>() {
        });
    }

    public JwtConnection create(JwtConnectionBody body) {
        return create(body, null);
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public JwtConnection create(JwtConnectionBody body, String idempotencyKey) {
        return t.post("/v1/jwt_connections", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public JwtConnection update(String id, JwtConnectionBody body) {
        return t.patch("/v1/jwt_connections/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** Returns {@code {object: "jwt_connection", id, deleted: true}}. */
    public DeletedObject delete(String id) {
        return t.delete("/v1/jwt_connections/" + enc(id), new TypeReference<>() {
        });
    }

    /**
     * Where to send the person's browser to sign in with a trusted JWT ({@code GET /v1/sso/jwt/{connectionId}}).
     * {@code loginUrl} is the connection's {@code login_url}. A URL, not an API call: the token works once,
     * and the 302 carries a one-time ticket that has to reach the browser.
     */
    public static String signInUrl(String loginUrl, String token, String redirectUrl) {
        return loginUrl + (loginUrl.contains("?") ? "&" : "?") + "token=" + enc(token) + "&redirect_url=" + enc(redirectUrl);
    }
}
