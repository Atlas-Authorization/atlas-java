package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AttackProtectionCaptcha(
        String provider,
        boolean siteKeySet,
        boolean secretSet,
        String managedBy) {
}
