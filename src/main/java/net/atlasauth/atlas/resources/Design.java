package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AtlasDesign;
import net.atlasauth.atlas.models.DataList;
import net.atlasauth.atlas.models.DeletedDesignAsset;
import net.atlasauth.atlas.models.DesignAsset;
import net.atlasauth.atlas.models.DesignCatalog;
import net.atlasauth.atlas.models.DesignBody;
import net.atlasauth.atlas.models.DesignPreview;
import net.atlasauth.atlas.models.DesignSettings;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Sign-in page design: the instance design, per-organization overrides, previews, uploaded images and the public design catalog. */
public final class Design {

    private final Transport t;

    public Design(Transport t) {
        this.t = t;
    }

    /**
     * {@code GET /v1/design/catalog} — The public design catalog: presets (each fully
     * resolved), the curated fonts, and the {@code --atlas-*} CSS variables.
     */
    public DesignCatalog catalog() {
        return t.get("/v1/design/catalog", new TypeReference<>() {
        });
    }

    /** Upload raw image bytes ({@code contentType} e.g. {@code image/png}) as a background. */
    public DesignAsset uploadAsset(byte[] data, String contentType) {
        return uploadAsset(data, contentType, null);
    }

    /** {@code POST /v1/design/assets} — Upload a design image (a background or a logo) for this instance. */
    public DesignAsset uploadAsset(byte[] data, String contentType, String kind) {
        Map<String, Object> q = Params.of().set("kind", kind).map();
        return t.requestBytes("POST", "/v1/design/assets", q, data, contentType, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/design/assets} — List this instance's uploaded design images, newest first. */
    public DataList<DesignAsset> listAssets() {
        return t.get("/v1/design/assets", new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/design/assets/{id}} — Delete one of this instance's design images. */
    public DeletedDesignAsset deleteAsset(String id) {
        return t.delete("/v1/design/assets/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/design} — Read the instance's sign-in page design: stored overrides, the resolved design, contrast warnings and the legacy flag. */
    public DesignSettings get() {
        return t.get("/v1/instance/design", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/instance/design} — Replace the instance design ({ design: AtlasDesign }). */
    public DesignSettings replace(DesignBody body) {
        return t.put("/v1/instance/design", body, new TypeReference<>() {
        });
    }

    /** Shorthand: wraps {@code design} in a {@link DesignBody}. */
    public DesignSettings replace(AtlasDesign design) {
        return replace(new DesignBody().design(design));
    }

    /** {@code PATCH /v1/instance/design} — Change part of the instance design ({ design: AtlasDesignPatch }): nested settings merge, null removes a setting. */
    public DesignSettings update(DesignBody body) {
        return t.patch("/v1/instance/design", body, new TypeReference<>() {
        });
    }

    /** Shorthand: wraps {@code design} in a {@link DesignBody}. */
    public DesignSettings update(AtlasDesign design) {
        return update(new DesignBody().design(design));
    }

    /** {@code POST /v1/instance/design/preview} — Compile an unsaved design: CSS variables scoped to .atlas-root, data attributes, font stylesheet URLs, warnings, the resolved design and the rendered sign-in page. */
    public DesignPreview preview(DesignBody body) {
        return t.post("/v1/instance/design/preview", body, new TypeReference<>() {
        });
    }

    /** Shorthand: wraps {@code design} in a {@link DesignBody}. */
    public DesignPreview preview(AtlasDesign design) {
        return preview(new DesignBody().design(design));
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public DesignPreview preview(DesignBody body, String idempotencyKey) {
        return t.post("/v1/instance/design/preview", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/organizations/{id}/design} — Read an organization's design override and the result of layering it over the instance design. */
    public DesignSettings getForOrganization(String id) {
        return t.get("/v1/organizations/" + enc(id) + "/design", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/organizations/{id}/design} — Replace an organization's design override ({ design: AtlasDesign }); { design: {} } removes it. */
    public DesignSettings replaceForOrganization(String id, DesignBody body) {
        return t.put("/v1/organizations/" + enc(id) + "/design", body, new TypeReference<>() {
        });
    }

    /** Shorthand: wraps {@code design} in a {@link DesignBody}. */
    public DesignSettings replaceForOrganization(String id, AtlasDesign design) {
        return replaceForOrganization(id, new DesignBody().design(design));
    }

    /** {@code PATCH /v1/organizations/{id}/design} — Change part of an organization's design override; null removes a setting. */
    public DesignSettings updateForOrganization(String id, DesignBody body) {
        return t.patch("/v1/organizations/" + enc(id) + "/design", body, new TypeReference<>() {
        });
    }

    /** Shorthand: wraps {@code design} in a {@link DesignBody}. */
    public DesignSettings updateForOrganization(String id, AtlasDesign design) {
        return updateForOrganization(id, new DesignBody().design(design));
    }
}
