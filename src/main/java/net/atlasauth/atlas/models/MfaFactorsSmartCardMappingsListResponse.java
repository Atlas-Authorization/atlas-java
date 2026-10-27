package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/users/{id}/smartcard_mappings}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MfaFactorsSmartCardMappingsListResponse(
        String object,
        List<MfaFactorsSmartCardMappingsListResponseDataItem> data) {
}
