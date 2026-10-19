package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A connected space and where readers redeem tokens. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ForumulateSpace(
        String id,
        String redeemUrl) {
}
