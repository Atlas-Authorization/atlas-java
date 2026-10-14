package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BillingUsageRecorded(
        String object,
        boolean recorded,
        boolean metered,
        double total) {
}
