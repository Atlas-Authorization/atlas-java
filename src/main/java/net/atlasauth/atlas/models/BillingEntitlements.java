package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** What an entitlement provider (Autumn, Commet) reports a subscription grants. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BillingEntitlements(List<String> features, Map<String, Double> limits) {
}
