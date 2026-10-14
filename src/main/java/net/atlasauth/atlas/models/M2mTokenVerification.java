package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record M2mTokenVerification(
        String object,
        boolean valid,
        String machineId,
        String name) {
}
