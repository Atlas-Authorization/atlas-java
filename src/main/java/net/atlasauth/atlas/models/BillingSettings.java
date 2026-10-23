package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * How numeric limits merge when a subject holds several entitling subscriptions:
 * {@code max} (default) or {@code sum}, per limit key.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BillingSettings(String object, String defaultLimitMerge, Map<String, String> limitMerge) {
}
