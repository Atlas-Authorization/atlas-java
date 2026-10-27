package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/desktop_mfa/devices}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DesktopMfaListDevicesResponse(
        String object,
        List<DesktopMfaListDevicesResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
