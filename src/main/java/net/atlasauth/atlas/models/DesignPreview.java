package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DesignPreview(
        String object,
        String scope,
        String css,
        Map<String, String> dataAttributes,
        List<String> fontStylesheets,
        ResolvedDesign resolved,
        List<DesignWarning> warnings,
        String html) {
}
