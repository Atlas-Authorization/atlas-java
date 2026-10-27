package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/iga/users/{id}/offboard}. */
public final class IgaLifecycleOffboardBody {
    public Boolean closeAccount;
    public Long graceSeconds;
    public String reason;

    public IgaLifecycleOffboardBody() {
    }

    public IgaLifecycleOffboardBody closeAccount(Boolean v) { this.closeAccount = v; return this; }
    public IgaLifecycleOffboardBody graceSeconds(Long v) { this.graceSeconds = v; return this; }
    public IgaLifecycleOffboardBody reason(String v) { this.reason = v; return this; }
}
