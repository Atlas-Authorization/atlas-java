package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AttackProtectionBruteForceTier(
        int threshold,
        long windowMs,
        long lockMs,
        boolean notifyUser,
        boolean flagForAdmin) {
}
