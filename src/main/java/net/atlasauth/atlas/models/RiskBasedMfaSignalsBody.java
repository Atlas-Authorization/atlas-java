package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Partial signal-toggle patch nested in {@link UpdateRiskBasedMfaBody}. Wire
 * fields are camelCase, so each is pinned with {@code @JsonProperty}.
 */
public final class RiskBasedMfaSignalsBody {
    @JsonProperty("newDevice")
    public Boolean newDevice;
    @JsonProperty("newSubnet")
    public Boolean newSubnet;
    @JsonProperty("newIp")
    public Boolean newIp;
    public Boolean velocity;
    @JsonProperty("impossibleTravel")
    public Boolean impossibleTravel;

    public RiskBasedMfaSignalsBody newDevice(Boolean v) { this.newDevice = v; return this; }
    public RiskBasedMfaSignalsBody newSubnet(Boolean v) { this.newSubnet = v; return this; }
    public RiskBasedMfaSignalsBody newIp(Boolean v) { this.newIp = v; return this; }
    public RiskBasedMfaSignalsBody velocity(Boolean v) { this.velocity = v; return this; }
    public RiskBasedMfaSignalsBody impossibleTravel(Boolean v) { this.impossibleTravel = v; return this; }
}
