package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/** What the renderers actually use — everything already escaped or dropped. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BrandingResolved(
        @JsonProperty("applicationName") String applicationName,
        @JsonProperty("escapedName") String escapedName,
        @JsonProperty("logoUrl") String logoUrl,
        @JsonProperty("colorPrimary") String colorPrimary,
        @JsonProperty("colorBackground") String colorBackground,
        @JsonProperty("colorText") String colorText,
        @JsonProperty("supportEmail") String supportEmail,
        /** Every audience the hosted page can theme for, as it reads them. */ @JsonProperty("audiences") Map<String, BrandingAudience> audiences) {
    /** The 1.4 shape, without {@code audiences}. */
    public BrandingResolved(String applicationName, String escapedName, String logoUrl, String colorPrimary, String colorBackground, String colorText, String supportEmail) {
        this(applicationName, escapedName, logoUrl, colorPrimary, colorBackground, colorText, supportEmail, null);
    }
}
