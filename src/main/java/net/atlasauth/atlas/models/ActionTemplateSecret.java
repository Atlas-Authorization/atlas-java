package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A secret a template reads, declared by NAME; values are never part of a template. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActionTemplateSecret(String name, String description, boolean required) {
}
