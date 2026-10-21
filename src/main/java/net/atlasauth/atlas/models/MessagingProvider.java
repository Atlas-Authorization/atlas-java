package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * A configured messaging provider. A provider SECRET is write-only: it goes in
 * through {@code config} and is never read back — a response reports only WHICH
 * secret keys are set ({@code secrets_set}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessagingProvider(
        String object,
        String channel,
        String transport,
        String fromAddress,
        String status,
        Map<String, Object> config,
        Map<String, Boolean> secretsSet,
        long updatedAt,
        /** The provider id: pass it as {@code provider_id} to managed email or the development sender. */ String id,
        /** {@code standard}, {@code development} or {@code managed_domain}. */ String mode) {
    /** The 1.4 shape, without {@code id}, {@code mode}. */
    public MessagingProvider(String object, String channel, String transport, String fromAddress, String status, Map<String, Object> config, Map<String, Boolean> secretsSet, long updatedAt) {
        this(object, channel, transport, fromAddress, status, config, secretsSet, updatedAt, null, null);
    }
}
