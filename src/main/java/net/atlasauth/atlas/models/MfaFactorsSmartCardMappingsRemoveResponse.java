package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/users/{id}/smartcard_mappings/{mappingId}}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MfaFactorsSmartCardMappingsRemoveResponse(
        String object,
        String id,
        Boolean deleted) {
}
