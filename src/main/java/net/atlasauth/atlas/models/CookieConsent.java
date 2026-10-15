package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * The consent-management platform (CMP) injected into the hosted pages. enabled:false (and no other fields) when none is set.
 *
 * @param settingsLink the "Cookie settings" link shown on the hosted pages, or null when there is none
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CookieConsent(
        String object,
        boolean enabled,
        String scriptUrl,
        String scriptId,
        String siteId,
        String blockingMode,
        List<String> extraOrigins,
        CookieConsentSettingsLink settingsLink) {

    /** The 1.2 shape, without {@code settingsLink}. */
    public CookieConsent(String object, boolean enabled, String scriptUrl, String scriptId, String siteId,
                         String blockingMode, List<String> extraOrigins) {
        this(object, enabled, scriptUrl, scriptId, siteId, blockingMode, extraOrigins, null);
    }
}
