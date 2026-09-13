package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AttackProtectionBruteForce(
        boolean enabled,
        List<AttackProtectionBruteForceTier> tiers) {
}
