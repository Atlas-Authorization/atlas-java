package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A provider-specific option beyond credentials (Google {@code hd}, tenant, prompt…). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthProviderSetting(
        String key,
        String label,
        /** One of "string", "boolean", "select". */
        String type,
        List<String> options,
        String help) {
}
