package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A list row: the built-in {@code default} and the stored {@code override} are
 * both returned so a caller can show a diff rather than guess what changed.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EmailTemplate(
        String object,
        String name,
        @JsonProperty("default") EmailTemplateBody defaultBody,
        EmailTemplateBody override,
        boolean customised) {
}
