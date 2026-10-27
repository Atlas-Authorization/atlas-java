package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicyBundlesListResponseDataItem(
        String object,
        String revision,
        String sha256,
        Long size,
        String keyId,
        String algorithm,
        String signature,
        String origin,
        Map<String, Object> manifest,
        String bundle,
        String mediaType,
        Long createdAt) {
}
