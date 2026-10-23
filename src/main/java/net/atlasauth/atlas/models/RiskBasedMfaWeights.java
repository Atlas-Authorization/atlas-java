package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Per-signal score weights that feed the risk total. These fields are camelCase
 * <em>on the wire</em>, so each is pinned with {@code @JsonProperty}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RiskBasedMfaWeights(
        @JsonProperty("newDevice") double newDevice,
        @JsonProperty("newSubnet") double newSubnet,
        @JsonProperty("newIp") double newIp,
        @JsonProperty("velocity") double velocity,
        @JsonProperty("impossibleTravel") double impossibleTravel,
        /** The account was idle past the dormant-account threshold before this sign-in. */ @JsonProperty("dormantAccount") double dormantAccount) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public RiskBasedMfaWeights(double newDevice, double newSubnet, double newIp, double velocity, double impossibleTravel) {
        this(newDevice, newSubnet, newIp, velocity, impossibleTravel, 0.0);
    }
}
