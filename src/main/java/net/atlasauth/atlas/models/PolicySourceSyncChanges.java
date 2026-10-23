package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** What a sync changed, as policy keys. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicySourceSyncChanges(
        List<String> created,
        List<String> updated,
        List<String> published,
        List<String> unchanged,
        List<String> rejected,
        List<String> retired) {
}
