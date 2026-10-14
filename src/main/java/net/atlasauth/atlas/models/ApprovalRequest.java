package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApprovalRequest(
        String object,
        String id,
        String status,
        String description,
        Map<String, String> context,
        String approvedByDeviceId,
        Long createdAt,
        Long expiresAt,
        Long resolvedAt) {
}
