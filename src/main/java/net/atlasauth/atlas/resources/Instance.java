package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.InstanceConfig;
import net.atlasauth.atlas.models.InstanceUpdateResult;
import net.atlasauth.atlas.models.UpdateInstanceBody;

/** The {@code /v1/instance} configuration resource. */
public final class Instance {

    private final Transport t;

    public Instance(Transport t) {
        this.t = t;
    }

    public InstanceConfig get() {
        return t.get("/v1/instance", new TypeReference<>() {
        });
    }

    public InstanceUpdateResult update(UpdateInstanceBody body) {
        return t.patch("/v1/instance", body, new TypeReference<>() {
        });
    }
}
