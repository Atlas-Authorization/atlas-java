package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Who created a provisioned account. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdProvisionedAccountActor(String type, String id) {
}
