package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The deletion acknowledgement for a messaging channel. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeletedMessagingProvider(
        String object,
        String channel,
        boolean deleted) {
}
