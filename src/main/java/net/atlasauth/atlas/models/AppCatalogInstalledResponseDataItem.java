package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AppCatalogInstalledResponseDataItem(
        String object,
        String id,
        String protocol,
        String name,
        String presetId,
        String entityId,
        String acsUrl,
        String clientId,
        String launchUrl,
        List<String> allowedEmailDomains,
        Map<String, Object> assignment) {
}
