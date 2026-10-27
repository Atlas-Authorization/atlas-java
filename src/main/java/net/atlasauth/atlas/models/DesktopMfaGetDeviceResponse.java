package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/desktop_mfa/devices/{id}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DesktopMfaGetDeviceResponse(
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
        Long createdAt,
        List<DesktopMfaGetDeviceResponseOfflineBundlesItem> offlineBundles) {
}
