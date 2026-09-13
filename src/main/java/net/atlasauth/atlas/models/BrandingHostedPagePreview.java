package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A draft rendered through the real hosted-page renderer, returned as HTML. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BrandingHostedPagePreview(
        String object,
        String html) {
}
