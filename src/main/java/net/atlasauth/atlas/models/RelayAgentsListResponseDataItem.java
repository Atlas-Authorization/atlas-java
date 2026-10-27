package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RelayAgentsListResponseDataItem(
        String object,
        String id,
        String name,
        String organizationId,
        String status,
        List<String> allowedDestinations,
        List<String> certFingerprints,
        String secretPrefix,
        Boolean online,
        Long lastSeenAt,
        String lastIp,
        String agentVersion,
        Long createdAt,
        String token) {
}
