package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/users/{id}/guardians}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersGuardiansRequestResponse(
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
        Long createdAt,
        String consentUrl,
        Boolean emailed) {
}
