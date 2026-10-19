package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** The before → after diff of an origin move. A repeated run returns no changes. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OriginMove(
        String object,
        String from,
        String to,
        boolean dryRun,
        /** True when this run wrote changes. */ boolean applied,
        List<OriginMoveChange> changes,
        List<OriginMoveProblem> problems,
        /** Platform app twin only: the app after the move. */ Map<String, Object> app) {
}
