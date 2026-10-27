package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/relay_agents}. Required: name. */
public final class RelayAgentsCreateBody {
    /** Required. */
    public String name;
    public String organizationId;
    public String status;
    /** host:port destinations the agent may reach. */
    public List<String> allowedDestinations;
    /** Client-certificate pins. */
    public List<String> certFingerprints;

    public RelayAgentsCreateBody() {
    }

    public RelayAgentsCreateBody name(String v) { this.name = v; return this; }
    public RelayAgentsCreateBody organizationId(String v) { this.organizationId = v; return this; }
    public RelayAgentsCreateBody status(String v) { this.status = v; return this; }
    public RelayAgentsCreateBody allowedDestinations(List<String> v) { this.allowedDestinations = v; return this; }
    public RelayAgentsCreateBody certFingerprints(List<String> v) { this.certFingerprints = v; return this; }
}
