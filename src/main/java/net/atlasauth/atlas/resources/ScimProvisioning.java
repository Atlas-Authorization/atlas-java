package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateScimProvisioningTargetBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.ScimProvisioningGroupSyncResult;
import net.atlasauth.atlas.models.ScimProvisioningSyncResult;
import net.atlasauth.atlas.models.ScimProvisioningTarget;
import net.atlasauth.atlas.models.ScimProvisioningTestResult;
import net.atlasauth.atlas.models.UpdateScimProvisioningTargetBody;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/scim_provisioning_targets} resource (outbound SCIM). */
public final class ScimProvisioning {

    private final Transport t;

    public ScimProvisioning(Transport t) {
        this.t = t;
    }

    public ListPage<ScimProvisioningTarget> list() {
        return t.get("/v1/scim_provisioning_targets", new TypeReference<>() {
        });
    }

    public ScimProvisioningTarget get(String id) {
        return t.get("/v1/scim_provisioning_targets/" + enc(id), new TypeReference<>() {
        });
    }

    public ScimProvisioningTarget create(CreateScimProvisioningTargetBody body) {
        return create(body, null);
    }

    public ScimProvisioningTarget create(CreateScimProvisioningTargetBody body, String idempotencyKey) {
        return t.post("/v1/scim_provisioning_targets", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public ScimProvisioningTarget update(String id, UpdateScimProvisioningTargetBody body) {
        return t.patch("/v1/scim_provisioning_targets/" + enc(id), body, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/scim_provisioning_targets/" + enc(id), new TypeReference<>() {
        });
    }

    /** Probe connectivity + auth to the downstream with the real bearer. Writes nothing. */
    public ScimProvisioningTestResult test(String id) {
        return t.post("/v1/scim_provisioning_targets/" + enc(id) + "/test", null, new TypeReference<>() {
        });
    }

    /** Force one user's sync now (backfill / re-push), out of band from the worker. */
    public ScimProvisioningSyncResult syncUser(String id, String userId) {
        var body = Params.of("user_id", userId).map();
        return t.post("/v1/scim_provisioning_targets/" + enc(id) + "/sync_user", body, new TypeReference<>() {
        });
    }

    /** Force one org's (group) sync now; only already-provisioned members are sent. */
    public ScimProvisioningGroupSyncResult syncGroup(String id, String organizationId) {
        var body = Params.of("organization_id", organizationId).map();
        return t.post("/v1/scim_provisioning_targets/" + enc(id) + "/sync_group", body, new TypeReference<>() {
        });
    }
}
