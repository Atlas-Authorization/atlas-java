package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CookieConsent;
import net.atlasauth.atlas.models.CookieConsentBody;
import net.atlasauth.atlas.models.InstanceConfig;
import net.atlasauth.atlas.models.InstanceUpdateResult;
import net.atlasauth.atlas.models.Preflight;
import net.atlasauth.atlas.models.UpdateInstanceBody;

/** The {@code /v1/instance} configuration resource. */
public final class Instance {

    private final Transport t;

    public Instance(Transport t) {
        this.t = t;
    }

    public InstanceConfig get() {
        return t.get("/v1/instance", new TypeReference<>() {
        });
    }

    public InstanceUpdateResult update(UpdateInstanceBody body) {
        return t.patch("/v1/instance", body, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/preflight} — Check your setup in one call. */
    public Preflight preflight() {
        return t.get("/v1/instance/preflight", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/cookie_consent} — Read the cookie-consent CMP injected into the hosted sign-in, sign-up and account pages (enabled:false when none). */
    public CookieConsent getCookieConsent() {
        return t.get("/v1/instance/cookie_consent", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/instance/cookie_consent} — Set the cookie-consent CMP for the hosted pages (any provider). */
    public CookieConsent replaceCookieConsent(CookieConsentBody body) {
        return t.put("/v1/instance/cookie_consent", body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/cookie_consent} — Turn off the hosted-page cookie-consent CMP: no script is injected and the CSP goes back to normal. */
    public CookieConsent deleteCookieConsent() {
        return t.delete("/v1/instance/cookie_consent", new TypeReference<>() {
        });
    }
}
