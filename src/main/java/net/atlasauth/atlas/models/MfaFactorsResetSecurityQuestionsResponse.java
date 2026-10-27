package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/users/{id}/security_questions}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MfaFactorsResetSecurityQuestionsResponse(
        String object,
        String userId,
        Boolean deleted) {
}
