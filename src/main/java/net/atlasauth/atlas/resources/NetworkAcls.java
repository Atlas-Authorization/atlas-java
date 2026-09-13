package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateNetworkAclBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.NetworkAcl;
import net.atlasauth.atlas.models.UpdateNetworkAclBody;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/network_acls} resource. */
public final class NetworkAcls {

    private final Transport t;

    public NetworkAcls(Transport t) {
        this.t = t;
    }

    /** All rules for the instance, in priority order. */
    public ListPage<NetworkAcl> list() {
        return t.get("/v1/network_acls", new TypeReference<>() {
        });
    }

    public NetworkAcl get(String id) {
        return t.get("/v1/network_acls/" + enc(id), new TypeReference<>() {
        });
    }

    public NetworkAcl create(CreateNetworkAclBody body) {
        return create(body, null);
    }

    public NetworkAcl create(CreateNetworkAclBody body, String idempotencyKey) {
        return t.post("/v1/network_acls", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public NetworkAcl update(String id, UpdateNetworkAclBody body) {
        return t.patch("/v1/network_acls/" + enc(id), body, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/network_acls/" + enc(id), new TypeReference<>() {
        });
    }
}
