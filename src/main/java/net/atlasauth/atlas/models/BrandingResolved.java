package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** What the renderers actually use — everything already escaped or dropped. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BrandingResolved(
        @JsonProperty("applicationName") String applicationName,
        @JsonProperty("escapedName") String escapedName,
        @JsonProperty("logoUrl") String logoUrl,
        @JsonProperty("colorPrimary") String colorPrimary,
        @JsonProperty("colorBackground") String colorBackground,
        @JsonProperty("colorText") String colorText,
        @JsonProperty("supportEmail") String supportEmail) {
}
