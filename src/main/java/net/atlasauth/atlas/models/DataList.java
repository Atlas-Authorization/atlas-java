package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A bare {@code { data }} list with no pagination metadata (an {@code object}
 * field may or may not be present). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DataList<T>(
        String object,
        List<T> data) {
}
