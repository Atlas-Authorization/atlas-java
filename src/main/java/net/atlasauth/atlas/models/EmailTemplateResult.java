package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The lean shape an update/delete acknowledges with (no {@code default}). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EmailTemplateResult(
        String object,
        String name,
        EmailTemplateBody override,
        boolean customised) {
}
