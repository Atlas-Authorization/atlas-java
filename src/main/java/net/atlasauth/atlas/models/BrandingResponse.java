package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A read/update echoes the stored branding plus {@code resolved} — the sanitized
 * shape the page will actually render, so a value silently dropped shows up here
 * rather than being discovered on a live page.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class BrandingResponse extends BrandingSettings {
    public String object;
    public BrandingResolved resolved;

    public BrandingResponse() {
    }
}
