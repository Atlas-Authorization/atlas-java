package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/vc/offers}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VcCreateOfferResponse(
        String object,
        String id,
        Long expiresAt,
        Map<String, Object> credentialOffer,
        String credentialOfferUri) {
}
