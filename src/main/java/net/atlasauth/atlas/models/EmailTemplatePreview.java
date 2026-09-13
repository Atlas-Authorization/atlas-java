package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * A rendered template with SAMPLE variables (never a real code). {@code valid} is
 * false with a {@code problem} when the draft would be dropped by the renderer.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EmailTemplatePreview(
        String object,
        String template,
        boolean valid,
        String problem,
        String html,
        Map<String, String> variables) {
}
