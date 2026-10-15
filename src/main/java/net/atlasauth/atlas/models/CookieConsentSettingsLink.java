package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * The "Cookie settings" link shown on the hosted pages, as returned on
 * {@link CookieConsent#settingsLink()}.
 *
 * @param label              the link text
 * @param preset             the CMP whose settings dialog the link opens ({@link #PRESET_COOKIEMUNCH},
 *                           {@link #PRESET_COOKIEBOT}, {@link #PRESET_ONETRUST}, {@link #PRESET_CUSTOM}),
 *                           or null; kept as a string so a value added later still decodes
 * @param attributes         the attributes you set
 * @param renderedAttributes the attributes the rendered link carries: yours plus the preset's trigger
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CookieConsentSettingsLink(
        String label,
        String preset,
        Map<String, String> attributes,
        Map<String, String> renderedAttributes) {

    public static final String PRESET_COOKIEMUNCH = "cookiemunch";
    public static final String PRESET_COOKIEBOT = "cookiebot";
    public static final String PRESET_ONETRUST = "onetrust";
    public static final String PRESET_CUSTOM = "custom";
}
