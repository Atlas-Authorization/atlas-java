package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A contrast warning (advice, never a refusal): a text/background pair below WCAG AA 4.5:1. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DesignWarning(
        String code,
        String mode,
        List<String> pair,
        double ratio,
        double required,
        String path,
        String message) {
}
