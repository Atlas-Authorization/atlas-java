package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The result of a test back-channel logout, recorded in the delivery log with {@code test: true}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BackchannelLogoutTest(
        String object,
        String deliveryId,
        /** True when the receiver answered 2xx. */ boolean ok,
        String sid,
        Integer status,
        Integer latencyMs,
        String error,
        String location,
        /** Set when nothing was sent (private, loopback or non-https address). */ String blockedReason,
        String bodyExcerpt) {
}
