package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateRadiusClientBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.RadiusClient;
import net.atlasauth.atlas.models.UpdateRadiusClientBody;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/radius_clients} resource (RADIUS NAS clients). */
public final class RadiusClients {

    private final Transport t;

    public RadiusClients(Transport t) {
        this.t = t;
    }

    public ListPage<RadiusClient> list() {
        return t.get("/v1/radius_clients", new TypeReference<>() {
        });
    }

    public RadiusClient create(CreateRadiusClientBody body) {
        return create(body, null);
    }

    public RadiusClient create(CreateRadiusClientBody body, String idempotencyKey) {
        return t.post("/v1/radius_clients", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public RadiusClient get(String id) {
        return t.get("/v1/radius_clients/" + enc(id), new TypeReference<>() {
        });
    }

    public RadiusClient update(String id, UpdateRadiusClientBody body) {
        return t.patch("/v1/radius_clients/" + enc(id), body, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/radius_clients/" + enc(id), new TypeReference<>() {
        });
    }
}
