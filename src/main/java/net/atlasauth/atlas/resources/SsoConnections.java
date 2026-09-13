package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateSsoConnectionBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.SsoConnection;
import net.atlasauth.atlas.models.SsoConnectionSamlMetadata;
import net.atlasauth.atlas.models.UpdateSsoConnectionBody;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/sso_connections} resource. */
public final class SsoConnections {

    private final Transport t;

    public SsoConnections(Transport t) {
        this.t = t;
    }

    public ListPage<SsoConnection> list() {
        return t.get("/v1/sso_connections", new TypeReference<>() {
        });
    }

    public SsoConnection get(String id) {
        return t.get("/v1/sso_connections/" + enc(id), new TypeReference<>() {
        });
    }

    public SsoConnection create(CreateSsoConnectionBody body) {
        return create(body, null);
    }

    public SsoConnection create(CreateSsoConnectionBody body, String idempotencyKey) {
        return t.post("/v1/sso_connections", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public SsoConnection update(String id, UpdateSsoConnectionBody body) {
        return t.patch("/v1/sso_connections/" + enc(id), body, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/sso_connections/" + enc(id), new TypeReference<>() {
        });
    }

    /** SP SAML metadata for a connection (JSON envelope carrying the XML). */
    public SsoConnectionSamlMetadata samlMetadata(String id) {
        return t.get("/v1/sso_connections/" + enc(id) + "/saml_metadata", new TypeReference<>() {
        });
    }
}
