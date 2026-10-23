package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ActionGalleryEntry;
import net.atlasauth.atlas.models.ActionGalleryInstallation;
import net.atlasauth.atlas.models.ListPage;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * The community Actions gallery: browse reusable Action templates (first-party and
 * community), install one as a DISABLED action, and keep installed actions up to date.
 */
public final class ActionGallery {

    private final Transport t;

    public ActionGallery(Transport t) {
        this.t = t;
    }

    /** The gallery, best first. Every filter may be null; {@code source} is builtin or community. */
    public ListPage<ActionGalleryEntry> list(String q, String category, String trigger, String source) {
        Map<String, Object> query = Params.of().set("q", q).set("category", category).set("trigger", trigger)
                .set("source", source).map();
        return t.get("/v1/action_gallery", query, new TypeReference<>() {
        });
    }

    /** One template with its code, secret names and versions. */
    public ActionGalleryEntry get(String slug) {
        return t.get("/v1/action_gallery/" + enc(slug), new TypeReference<>() {
        });
    }

    /**
     * Install as a new, disabled action ({@code action}, {@code template},
     * {@code install_id}, {@code missing_secrets}). {@code version} and {@code name} may be
     * null; {@code secrets} supplies the values of the secrets the template declares.
     */
    public JsonNode install(String slug, String version, String name, Map<String, String> secrets) {
        Map<String, Object> body = Params.of().set("version", version).set("name", name).set("secrets", secrets).map();
        return t.post("/v1/action_gallery/" + enc(slug) + "/install", body, new TypeReference<>() {
        });
    }

    /** The actions installed from the gallery, with update status. */
    public ListPage<ActionGalleryInstallation> installs() {
        return t.get("/v1/action_gallery_installs", new TypeReference<>() {
        });
    }

    /** Move an installed action to its template's latest version ({@code secrets} may be null). */
    public JsonNode update(String actionId, Map<String, String> secrets) {
        return t.post("/v1/actions/" + enc(actionId) + "/gallery_update", Params.of().set("secrets", secrets).map(),
                new TypeReference<>() {
                });
    }
}
