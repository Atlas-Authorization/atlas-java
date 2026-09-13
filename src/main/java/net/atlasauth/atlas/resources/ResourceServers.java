package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateResourceServerBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.ResourceServer;
import net.atlasauth.atlas.models.UpdateResourceServerBody;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/resource_servers} resource. */
public final class ResourceServers {

    private final Transport t;

    public ResourceServers(Transport t) {
        this.t = t;
    }

    public ListPage<ResourceServer> list() {
        return t.get("/v1/resource_servers", new TypeReference<>() {
        });
    }

    public ResourceServer get(String id) {
        return t.get("/v1/resource_servers/" + enc(id), new TypeReference<>() {
        });
    }

    public ResourceServer create(CreateResourceServerBody body) {
        return t.post("/v1/resource_servers", body, new TypeReference<>() {
        });
    }

    public ResourceServer update(String id, UpdateResourceServerBody body) {
        return t.patch("/v1/resource_servers/" + enc(id), body, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/resource_servers/" + enc(id), new TypeReference<>() {
        });
    }
}
