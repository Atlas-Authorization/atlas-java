package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/relay_agents/{id}}. */
public final class RelayAgentsUpdateBody {
    public String name;
    public String organizationId;
    public String status;
    /** host:port destinations the agent may reach. */
    public List<String> allowedDestinations;
    /** Client-certificate pins. */
    public List<String> certFingerprints;

    public RelayAgentsUpdateBody() {
    }

    public RelayAgentsUpdateBody name(String v) { this.name = v; return this; }
    public RelayAgentsUpdateBody organizationId(String v) { this.organizationId = v; return this; }
    public RelayAgentsUpdateBody status(String v) { this.status = v; return this; }
    public RelayAgentsUpdateBody allowedDestinations(List<String> v) { this.allowedDestinations = v; return this; }
    public RelayAgentsUpdateBody certFingerprints(List<String> v) { this.certFingerprints = v; return this; }
}
