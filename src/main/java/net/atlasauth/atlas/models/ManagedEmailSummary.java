package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The managed sending domain as the provider list shows it. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ManagedEmailSummary(
        String object,
        String fromAddress,
        String domain,
        String status,
        boolean verified) {
}
