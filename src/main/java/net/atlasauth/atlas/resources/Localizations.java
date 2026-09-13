package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateLocalizationBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.Localization;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/localizations} resource. */
public final class Localizations {

    private final Transport t;

    public Localizations(Transport t) {
        this.t = t;
    }

    public ListPage<Localization> list() {
        return list(null, null, null);
    }

    public ListPage<Localization> list(String resourceType, String resourceName, String locale) {
        Map<String, Object> q = Params.of()
                .set("resource_type", resourceType)
                .set("resource_name", resourceName)
                .set("locale", locale)
                .map();
        return t.get("/v1/localizations", q, new TypeReference<>() {
        });
    }

    public Localization create(CreateLocalizationBody body) {
        return create(body, null);
    }

    public Localization create(CreateLocalizationBody body, String idempotencyKey) {
        return t.post("/v1/localizations", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public Localization get(String id) {
        return t.get("/v1/localizations/" + enc(id), new TypeReference<>() {
        });
    }

    public Localization update(String id, Map<String, Object> content, Boolean enabled) {
        Map<String, Object> body = Params.of().set("content", content).set("enabled", enabled).map();
        return t.patch("/v1/localizations/" + enc(id), body, new TypeReference<>() {
        });
    }

    public void delete(String id) {
        t.delete("/v1/localizations/" + enc(id), new TypeReference<Void>() {
        });
    }
}
