package net.atlasauth.atlas.models;

import java.util.Map;

/**
 * The "Cookie settings" link to show on the hosted pages, set with
 * {@link CookieConsentBody#settingsLink}. A link needs a {@code preset} or at least
 * one attribute (400 otherwise). Leave {@link CookieConsentBody#settingsLink} unset
 * for no link.
 */
public final class CookieConsentSettingsLinkInput {
    /** The link text, 1-60 characters. Required. */
    public String label;
    /**
     * The CMP whose settings dialog the link opens: one of the
     * {@code CookieConsentSettingsLink.PRESET_*} constants, or null for none.
     */
    public String preset;
    /**
     * Extra attributes for the link (at most 10, values up to 200 characters). Allowed
     * names: {@code data-*}, {@code aria-*}, {@code id}, {@code class} and {@code href}
     * (an https URL or a {@code #fragment}).
     */
    public Map<String, String> attributes;

    public CookieConsentSettingsLinkInput() {
    }

    public CookieConsentSettingsLinkInput(String label) {
        this.label = label;
    }

    public CookieConsentSettingsLinkInput label(String v) { this.label = v; return this; }
    public CookieConsentSettingsLinkInput preset(String v) { this.preset = v; return this; }
    public CookieConsentSettingsLinkInput attributes(Map<String, String> v) { this.attributes = v; return this; }
}
