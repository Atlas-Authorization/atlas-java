package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ActionTemplate;
import net.atlasauth.atlas.models.ActionTemplateBody;
import net.atlasauth.atlas.models.ActionTemplateSecret;
import net.atlasauth.atlas.models.ActionTemplateVersion;
import net.atlasauth.atlas.models.ListPage;

import java.util.List;

import static net.atlasauth.atlas.http.Transport.enc;

/** Action templates this instance publishes to the gallery (staff review them). */
public final class ActionTemplates {

    private final Transport t;

    public ActionTemplates(Transport t) {
        this.t = t;
    }

    public ListPage<ActionTemplate> list() {
        return t.get("/v1/action_templates", new TypeReference<>() {
        });
    }

    public ActionTemplate create(ActionTemplateBody body) {
        return t.post("/v1/action_templates", body, new TypeReference<>() {
        });
    }

    public ActionTemplate get(String id) {
        return t.get("/v1/action_templates/" + enc(id), new TypeReference<>() {
        });
    }

    public ActionTemplate update(String id, ActionTemplateBody body) {
        return t.patch("/v1/action_templates/" + enc(id), body, new TypeReference<>() {
        });
    }

    public JsonNode delete(String id) {
        return t.delete("/v1/action_templates/" + enc(id), new TypeReference<>() {
        });
    }

    /**
     * Add a version ({@code version} MAJOR.MINOR.PATCH, higher than every existing one).
     * {@code secrets} and {@code changelog} may be null; {@code submit} sends it to review.
     */
    public ActionTemplateVersion addVersion(String id, String version, String code, List<ActionTemplateSecret> secrets,
                                            String changelog, Boolean submit) {
        return t.post("/v1/action_templates/" + enc(id) + "/versions", Params.of().set("version", version)
                .set("code", code).set("secrets", secrets).set("changelog", changelog).set("submit", submit).map(),
                new TypeReference<>() {
                });
    }

    public ActionTemplateVersion submitVersion(String id, String version) {
        return t.post("/v1/action_templates/" + enc(id) + "/versions/" + enc(version) + "/submit", null,
                new TypeReference<>() {
                });
    }

    public JsonNode deleteVersion(String id, String version) {
        return t.delete("/v1/action_templates/" + enc(id) + "/versions/" + enc(version), new TypeReference<>() {
        });
    }

    /** List an approved template again after unlisting it. */
    public ActionTemplate publish(String id) {
        return t.post("/v1/action_templates/" + enc(id) + "/publish", null, new TypeReference<>() {
        });
    }

    /** Hide a template from the gallery (installs keep working). */
    public ActionTemplate unlist(String id) {
        return t.post("/v1/action_templates/" + enc(id) + "/unlist", null, new TypeReference<>() {
        });
    }
}
