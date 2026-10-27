package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VcCredentialTypesListResponseDataItem(
        String object,
        String id,
        String vct,
        String format,
        String namespace,
        List<String> iacaRoots,
        List<String> signerChain,
        String name,
        List<Map<String, Object>> claims,
        Long lifetimeSeconds,
        Boolean requireWalletAttestation,
        Long createdAt) {
}
