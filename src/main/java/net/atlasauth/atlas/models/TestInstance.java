package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TestInstance(
        String object,
        String instanceId,
        String environment,
        String publishableKey,
        Boolean created,
        String secretKey) {
}
