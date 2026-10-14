package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The consent-management platform (CMP) injected into the hosted pages. enabled:false (and no other fields) when none is set. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CookieConsent(
        String object,
        boolean enabled,
        String scriptUrl,
        String scriptId,
        String siteId,
        String blockingMode,
        List<String> extraOrigins) {
}
