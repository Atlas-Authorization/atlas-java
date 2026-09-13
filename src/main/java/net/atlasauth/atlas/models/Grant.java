package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** An OAuth consent grant — the scopes a user authorized a client for. Never a token. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Grant(
        String object,
        String id,
        String clientId,
        String clientName,
        List<String> scopes,
        long grantedAt,
        long updatedAt) {
}
