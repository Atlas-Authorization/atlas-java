package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DesktopMfaListDevicesResponseDataItem(
        String object,
        String id,
        String userId,
        String deviceKeyId,
        String hostname,
        String platform,
        String agentVersion,
        String status,
        Boolean offlinePinSet,
        Long bundleGeneration,
        Long lastSeenAt,
        Long revokedAt,
        Long createdAt) {
}
