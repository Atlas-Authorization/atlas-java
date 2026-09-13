package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Partial weights patch nested in {@link UpdateRiskBasedMfaBody}. Wire fields are
 * camelCase, so each is pinned with {@code @JsonProperty}.
 */
public final class RiskBasedMfaWeightsBody {
    @JsonProperty("newDevice")
    public Double newDevice;
    @JsonProperty("newSubnet")
    public Double newSubnet;
    @JsonProperty("newIp")
    public Double newIp;
    public Double velocity;
    @JsonProperty("impossibleTravel")
    public Double impossibleTravel;

    public RiskBasedMfaWeightsBody newDevice(Double v) { this.newDevice = v; return this; }
    public RiskBasedMfaWeightsBody newSubnet(Double v) { this.newSubnet = v; return this; }
    public RiskBasedMfaWeightsBody newIp(Double v) { this.newIp = v; return this; }
    public RiskBasedMfaWeightsBody velocity(Double v) { this.velocity = v; return this; }
    public RiskBasedMfaWeightsBody impossibleTravel(Double v) { this.impossibleTravel = v; return this; }
}
