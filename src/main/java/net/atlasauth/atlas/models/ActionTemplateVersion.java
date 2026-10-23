package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** One version of a template you publish ({@code draft}, {@code pending_review}, {@code approved} or {@code rejected}). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActionTemplateVersion(
        String object,
        String version,
        String status,
        String trigger,
        List<ActionTemplateSecret> secrets,
        String changelog,
        /** Why staff rejected it, when they did. */ String reviewNote,
        String code,
        Long submittedAt,
        Long reviewedAt,
        Long createdAt,
        boolean deleted) {
}
