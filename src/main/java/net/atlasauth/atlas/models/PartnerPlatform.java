package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * This instance as a partner platform of another product: the partner signs its deliveries with a published
 * Ed25519 key set and delivers signed consent events to this instance's receiver. The partner token is stored
 * once, encrypted, and is never part of a response: only {@code tokenSet} and {@code tokenPrefix} show it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PartnerPlatform(
        String object,
        String id,
        String partner,
        String name,
        boolean preset,
        String platformId,
        boolean enabled,
        String jwksUrl,
        String signatureHeader,
        /** {@code t_kid_ed25519}. */ String signatureScheme,
        int toleranceSeconds,
        List<String> allowedSources,
        boolean recordConsents,
        String receiverUrl,
        String receiverUrlTemplate,
        boolean servesDeployment,
        Registration registration,
        boolean tokenSet,
        String tokenPrefix,
        Long lastDeliveryAt,
        String lastDeliveryError,
        long createdAt,
        long updatedAt) {

    /** {@code status} is {@code unregistered} or {@code registered}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Registration(String status, Long registeredAt) {
    }
}
