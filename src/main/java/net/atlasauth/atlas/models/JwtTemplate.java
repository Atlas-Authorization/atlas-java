package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** A JWT template from {@code /v1/jwt_templates}. Keyed by {@code name}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JwtTemplate(
        String object,
        String name,
        Map<String, String> claims,
        /** The template's mint settings (never the HS256 secret). */ JwtTemplateSettingsView settings) {

    /** The shape before settings were reported. */
    public JwtTemplate(String object, String name, Map<String, String> claims) {
        this(object, name, claims, null);
    }
}
