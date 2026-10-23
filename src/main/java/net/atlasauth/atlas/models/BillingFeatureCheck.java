package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * May a subject use a feature? {@code source} is {@code provider} (asked live of
 * Autumn/Commet) or {@code atlas} (from the resolved entitlement and this period's usage).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BillingFeatureCheck(
        String object,
        String feature,
        boolean allowed,
        String source,
        String provider,
        Double remaining,
        Double granted,
        Double usage,
        boolean unlimited) {
}
