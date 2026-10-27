package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/vault/key_provider}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VaultKeyProviderGetResponse(
        String object,
        String id,
        String provider,
        Map<String, Object> config,
        Long updatedAt) {
}
