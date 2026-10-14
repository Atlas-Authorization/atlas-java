package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.RedirectUrl;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Allowlisted redirect URLs ({@code /v1/redirect_urls}). */
public final class RedirectUrls {

    private final Transport t;

    public RedirectUrls(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/redirect_urls} — List the allowlisted OAuth/SSO redirect URLs. */
    public ListPage<RedirectUrl> list() {
        return t.get("/v1/redirect_urls", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/redirect_urls} — Add a permitted OAuth/SSO redirect URL (absolute http(s); idempotent on the URL). */
    public RedirectUrl create(String url) {
        Map<String, Object> payload = Params.of().set("url", url).map();
        return t.post("/v1/redirect_urls", payload, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/redirect_urls/{id}} — Fetch one allowlisted redirect URL. */
    public RedirectUrl get(String id) {
        return t.get("/v1/redirect_urls/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/redirect_urls/{id}} — Remove a redirect URL from the allowlist. */
    public DeletedObject delete(String id) {
        return t.delete("/v1/redirect_urls/" + enc(id), new TypeReference<>() {
        });
    }
}
