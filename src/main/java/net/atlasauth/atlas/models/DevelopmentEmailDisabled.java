package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Acknowledges putting the email provider back in standard mode (the provider stays). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DevelopmentEmailDisabled(String object, boolean deleted, String providerId, String mode) {
}
