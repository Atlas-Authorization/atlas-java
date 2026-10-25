package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** An app a provisioned account is confined to. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdProvisionedAccountApp(String id, String clientId, String name) {
}
