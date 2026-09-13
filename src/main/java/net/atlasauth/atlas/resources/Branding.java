package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.BrandingHostedPagePreview;
import net.atlasauth.atlas.models.BrandingResponse;
import net.atlasauth.atlas.models.BrandingSettings;

/** The {@code /v1/branding} resource. */
public final class Branding {

    private final Transport t;

    public Branding(Transport t) {
        this.t = t;
    }

    public BrandingResponse get() {
        return t.get("/v1/branding", new TypeReference<>() {
        });
    }

    /** A PARTIAL merge — one field changes, the rest is preserved. */
    public BrandingResponse update(BrandingSettings body) {
        return t.patch("/v1/branding", body, new TypeReference<>() {
        });
    }

    /** Render a draft through the live sign-in renderer without saving it. */
    public BrandingHostedPagePreview preview(BrandingSettings body) {
        return t.post("/v1/branding/preview", body, new TypeReference<>() {
        });
    }
}
