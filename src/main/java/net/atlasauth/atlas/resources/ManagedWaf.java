package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ManagedWafOutcome;
import net.atlasauth.atlas.models.ManagedWafStatus;
import net.atlasauth.atlas.models.UpdateManagedWafBody;

/** The {@code /v1/managed_waf} resource. */
public final class ManagedWaf {

    private final Transport t;

    public ManagedWaf(Transport t) {
        this.t = t;
    }

    /** Read config + provisioning state, with secrets reduced to {@code has_*} markers. */
    public net.atlasauth.atlas.models.ManagedWaf get() {
        return t.get("/v1/managed_waf", new TypeReference<>() {
        });
    }

    /** Read just the provisioning state. */
    public ManagedWafStatus status() {
        return t.get("/v1/managed_waf/status", new TypeReference<>() {
        });
    }

    /** Set config and write-only credentials; omitted fields are left unchanged. */
    public net.atlasauth.atlas.models.ManagedWaf update(UpdateManagedWafBody body) {
        return t.put("/v1/managed_waf", body, new TypeReference<>() {
        });
    }

    /** Apply now — idempotent create/update + associate the WebACL. */
    public ManagedWafOutcome provision() {
        return t.post("/v1/managed_waf/provision", null, new TypeReference<>() {
        });
    }

    /** Disassociate and delete the WebACL. */
    public ManagedWafOutcome deprovision() {
        return t.post("/v1/managed_waf/deprovision", null, new TypeReference<>() {
        });
    }
}
