package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** An Atlas browser SDK version that called the instance in the last 7 days. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SdkVersionSeen(
        @com.fasterxml.jackson.annotation.JsonProperty("package") String packageName,
        String version,
        long lastSeenAt,
        List<SdkAdvisory> advisories) {
}
