package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * One risk-scored sign-in on the audit / shadow-mode timeline. {@code level} is
 * {@code low}/{@code medium}/{@code high}; {@code mode} is {@code enforce}/{@code audit}
 * (in audit mode {@code action} is what <em>would</em> have happened); {@code feedback}
 * is {@code legit}, {@code fraud} or {@code null}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RiskEvent(
        String object,
        String id,
        String userId,
        String level,
        double score,
        List<String> signals,
        String action,
        String mode,
        String ip,
        String feedback,
        String feedbackAt,
        String createdAt) {
}
