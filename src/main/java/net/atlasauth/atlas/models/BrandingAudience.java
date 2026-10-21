package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The hosted-page branding for one audience hint ({@code branding.audiences[hint]}). A sign-in
 * started with {@code audience_hint=<hint>} uses it.
 *
 * @param heading      replaces the card heading (up to 120 characters)
 * @param subheading   replaces the line under the heading (up to 240 characters)
 * @param logoUrl      replaces the logo (https)
 * @param primaryColor replaces the primary colour (hex, rgb() or a CSS colour name)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BrandingAudience(
        @JsonProperty("heading") String heading,
        @JsonProperty("subheading") String subheading,
        @JsonProperty("logoUrl") String logoUrl,
        @JsonProperty("primaryColor") String primaryColor) {
}
