package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersMinorStatusResponseGuardiansItem(
        String object,
        String id,
        String childUserId,
        String guardianUserId,
        String guardianEmail,
        String relationship,
        String status,
        Long expiresAt,
        String consentMethod,
        Long consentedAt,
        Long createdAt) {
}
