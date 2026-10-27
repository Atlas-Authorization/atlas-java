package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/vault/objects}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VaultCreateResponse(
        String object,
        String id,
        String name,
        Map<String, Object> keyContext,
        Map<String, Object> metadata,
        Long version,
        String keyProvider,
        Long createdAt,
        Long updatedAt) {
}
