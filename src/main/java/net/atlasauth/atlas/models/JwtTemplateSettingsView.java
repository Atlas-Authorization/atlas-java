package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The non-secret mint settings a template reports; the HS256 secret is never returned. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JwtTemplateSettingsView(
        Long lifetimeSeconds,
        String audience,
        String algorithm,
        Boolean hasSigningSecret) {
}
