package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The {@code { object: 'list', data, has_more }} envelope some list routes use. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ListPage<T>(
        String object,
        List<T> data,
        Boolean hasMore) {
}
