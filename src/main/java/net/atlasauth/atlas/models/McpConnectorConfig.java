package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record McpConnectorConfig(
        String object,
        Boolean enabled,
        String name,
        List<String> exposedScopes,
        Boolean dynamicRegistration,
        List<String> allowedRedirectHosts,
        String discoveryUrl,
        String resource,
        Boolean resourceServerExists) {
}
