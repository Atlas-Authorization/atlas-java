package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Acknowledgement that a fixed, clearly-marked test message was sent. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessagingTestResult(
        String object,
        String channel,
        String sentTo,
        boolean ok) {
}
