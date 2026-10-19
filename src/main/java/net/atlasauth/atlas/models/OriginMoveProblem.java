package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A rewritten value the field would refuse; a real run with problems is a 422 and writes nothing. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OriginMoveProblem(
        String object,
        String id,
        String field,
        String url,
        String message) {
}
