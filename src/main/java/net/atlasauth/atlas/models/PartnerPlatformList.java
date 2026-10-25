package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** {@code GET /v1/partner_platforms}: the configured partner platforms plus the ready-made presets. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PartnerPlatformList(
        String object,
        List<PartnerPlatform> data,
        boolean hasMore,
        List<JsonNode> presets) {
}
