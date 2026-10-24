package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The answer to {@code POST /v1/privacy/lookup}. {@code status} is {@code found} or {@code no_match}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PrivacyLookupResult(
        String object,
        String status,
        @JsonProperty("subjectId") String subjectId,
        /** atlas.sub, atlas.ext, email or email_unverified. */ @JsonProperty("matchedBy") String matchedBy,
        String detail,
        String principal) {
}
