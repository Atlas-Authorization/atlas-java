package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/iga/settings}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaLifecycleSettingsResponse(
        String object,
        Boolean jmlEnabled,
        String leaverAction,
        Long leaverCloseGraceSeconds) {
}
