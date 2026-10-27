package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/users/{id}/minor_status}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersMinorStatusResponse(
        String object,
        String status,
        String birthdate,
        Long ageAtAssessment,
        Long consentAge,
        String jurisdiction,
        Long consentedAt,
        List<UsersMinorStatusResponseGuardiansItem> guardians) {
}
