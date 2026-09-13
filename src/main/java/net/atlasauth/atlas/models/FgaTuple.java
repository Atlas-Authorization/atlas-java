package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A stored relationship tuple. {@code user} is {@code type:id} (or
 * {@code type:id#relation} for a userset); {@code target} is the object as
 * {@code type:id}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FgaTuple(
        String object,
        String user,
        String relation,
        String target,
        long createdAt) {
}
