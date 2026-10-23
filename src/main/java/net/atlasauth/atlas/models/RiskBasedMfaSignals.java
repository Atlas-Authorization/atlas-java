package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Per-signal on/off toggles; a disabled signal contributes nothing. These fields
 * are camelCase <em>on the wire</em>, so each is pinned with {@code @JsonProperty}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RiskBasedMfaSignals(
        @JsonProperty("newDevice") boolean newDevice,
        @JsonProperty("newSubnet") boolean newSubnet,
        @JsonProperty("newIp") boolean newIp,
        @JsonProperty("velocity") boolean velocity,
        @JsonProperty("impossibleTravel") boolean impossibleTravel,
        @JsonProperty("dormantAccount") boolean dormantAccount) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public RiskBasedMfaSignals(boolean newDevice, boolean newSubnet, boolean newIp, boolean velocity, boolean impossibleTravel) {
        this(newDevice, newSubnet, newIp, velocity, impossibleTravel, false);
    }
}
