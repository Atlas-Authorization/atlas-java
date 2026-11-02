package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Acknowledgement that a fixed, clearly-marked test message was sent. A provider refusal is a 502
 * {@code AtlasApiException} whose meta says what the provider answered, so {@code ok} is always true here.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessagingTestResult(
        String object,
        String channel,
        String sentTo,
        boolean ok,
        /** Whether the provider accepted the message. */ Boolean accepted,
        /** The transport that accepted it (resend, smtp, twilio, ...). */ String provider,
        /** The provider's id for the message, when it gave one. */ String providerMessageId,
        /** SMTP: the server reply line (carries the queue id). */ String providerResponse) {

    /** The 1.20 shape, without the provider outcome. */
    public MessagingTestResult(String object, String channel, String sentTo, boolean ok) {
        this(object, channel, sentTo, ok, null, null, null, null);
    }
}
