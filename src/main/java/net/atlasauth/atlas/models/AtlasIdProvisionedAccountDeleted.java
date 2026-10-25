package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The acknowledgement of removing a provisioned account. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdProvisionedAccountDeleted(String object, String id, boolean deleted, int sessionsEnded) {
}
