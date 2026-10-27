package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/flows/{id}/simulate}. Required: userId. */
public final class JourneysSimulateBody {
    /** Required. */
    public String userId;
    public String method;
    public Boolean mfaVerified;
    public String ip;
    public String applicationId;
    public Long version;

    public JourneysSimulateBody() {
    }

    public JourneysSimulateBody userId(String v) { this.userId = v; return this; }
    public JourneysSimulateBody method(String v) { this.method = v; return this; }
    public JourneysSimulateBody mfaVerified(Boolean v) { this.mfaVerified = v; return this; }
    public JourneysSimulateBody ip(String v) { this.ip = v; return this; }
    public JourneysSimulateBody applicationId(String v) { this.applicationId = v; return this; }
    public JourneysSimulateBody version(Long v) { this.version = v; return this; }
}
