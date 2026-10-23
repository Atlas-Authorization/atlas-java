package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A policy found in the feed by a source test. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicySourceTestPolicy(
        String key,
        String title,
        List<String> versions) {
}
