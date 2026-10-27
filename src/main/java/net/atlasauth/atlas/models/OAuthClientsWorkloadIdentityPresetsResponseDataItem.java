package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthClientsWorkloadIdentityPresetsResponseDataItem(
        String object,
        String kind,
        String title,
        String issuer,
        String jwksUri,
        String subjectExample,
        String notes) {
}
