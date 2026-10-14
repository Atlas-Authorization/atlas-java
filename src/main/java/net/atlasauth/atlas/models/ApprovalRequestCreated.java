package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApprovalRequestCreated(
        String object,
        String id,
        String status,
        String matchNumber,
        Long expiresAt) {
}
