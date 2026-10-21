package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The development email sender as the provider list shows it. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DevelopmentEmailSummary(String object, boolean enabled, String fromAddress) {
}
