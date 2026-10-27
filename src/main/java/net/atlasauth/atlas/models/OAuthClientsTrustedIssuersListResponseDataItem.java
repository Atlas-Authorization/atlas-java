package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsTrustedIssuersListResponseDataItem(
        String object,
        String id,
        String clientId,
        String issuer,
        Map<String, Object> jwks,
        String jwksUri,
        String subjectMapping,
        String workloadKind,
        String purpose,
        String subjectPattern,
        Map<String, Object> requiredClaims,
        List<String> audiences,
        Long createdAt) {
}
