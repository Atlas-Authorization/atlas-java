package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** One link from the instance to another platform, and its health. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InstanceConnection(
        String object,
        /** privacy_webhook, cmp, atlas_id_app or jwt_template. */ String kind,
        String check,
        boolean ok,
        /** error, warning or info. */ String severity,
        Map<String, String> subject,
        String fixHint,
        long lastCheckedAt,
        /** Kind-specific facts. */ Map<String, Object> details) {
}
