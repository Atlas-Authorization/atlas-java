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
        CookieConsentSettingsLink settingsLink,
        /** page (the script tag carries the page language) or browser. */ String culture,
        /** Every cookie and browser-storage key Atlas writes for this instance. */ List<DeclaredCookie> declaredCookies) {

    /** The previous shape, without {@code culture}, {@code declaredCookies}. */
    public CookieConsent(String object, boolean enabled, String scriptUrl, String scriptId, String siteId, String blockingMode, List<String> extraOrigins, CookieConsentSettingsLink settingsLink) {
        this(object, enabled, scriptUrl, scriptId, siteId, blockingMode, extraOrigins, settingsLink, null, null);
    }

    /** The 1.2 shape, without {@code settingsLink}. */
    public CookieConsent(String object, boolean enabled, String scriptUrl, String scriptId, String siteId,
                         String blockingMode, List<String> extraOrigins) {
        this(object, enabled, scriptUrl, scriptId, siteId, blockingMode, extraOrigins, null);
    }
}
