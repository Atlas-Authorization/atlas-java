package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProgressionEvent(
        String object,
        String track,
        long xp,
        long level,
        boolean levelChanged,
        long delta) {
}
