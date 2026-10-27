package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code DELETE /v1/vault/key_provider}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VaultKeyProviderRemoveResponse(
        String object,
        Boolean deleted) {
}
