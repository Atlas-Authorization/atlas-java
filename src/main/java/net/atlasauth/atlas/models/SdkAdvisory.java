package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** An advisory published for an Atlas browser SDK version range. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SdkAdvisory(
        String object,
        @com.fasterxml.jackson.annotation.JsonProperty("package") String packageName,
        String affected,
        String fixedIn,
        String reason,
        /** critical, high, medium or low. */ String severity,
        String summary,
        String publishedAt) {
}
