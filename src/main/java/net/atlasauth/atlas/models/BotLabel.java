package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A supervised training label, attachable after the interaction.
 * {@code subjectType} is one of {@code user}/{@code device}/{@code ip_hash}/{@code attempt};
 * {@code label} is one of {@code bot}/{@code human}/{@code suspect}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BotLabel(
        String object,
        String id,
        String subjectType,
        String subjectId,
        String label,
        String source,
        double confidence,
        String note,
        String labeledBy,
        long createdAt) {
}
