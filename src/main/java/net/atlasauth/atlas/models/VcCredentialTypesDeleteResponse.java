package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/vc/credential_types/{id}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VcCredentialTypesDeleteResponse(
        String object,
        String id,
        Boolean deleted) {
}
