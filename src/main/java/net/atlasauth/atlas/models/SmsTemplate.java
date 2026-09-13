package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A template row: table-backed, keyed by {@code name}. The built-in
 * {@code default} and the stored {@code body} override are both returned so a
 * caller can show a diff. {@code body} is null when no override is stored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SmsTemplate(
        String object,
        String name,
        @JsonProperty("default") String defaultBody,
        String body,
        boolean enabled,
        boolean customised,
        List<String> variables) {
}
