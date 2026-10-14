package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConnectorInstall(
        String object,
        String connector,
        String shareMode,
        String providerKey,
        Boolean needsCredentials) {
}
