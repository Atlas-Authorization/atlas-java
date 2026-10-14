package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.Connector;
import net.atlasauth.atlas.models.ConnectorBody;
import net.atlasauth.atlas.models.ConnectorInstall;
import net.atlasauth.atlas.models.ConnectorReview;
import net.atlasauth.atlas.models.ConnectorSharedCredentialsBody;
import net.atlasauth.atlas.models.DataList;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.DirectoryEntry;
import net.atlasauth.atlas.models.DirectoryParams;
import net.atlasauth.atlas.models.InstallConnectorBody;
import net.atlasauth.atlas.models.ListPage;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Community connector profiles ({@code /v1/connectors}) and the connector directory. */
public final class Connectors {

    private final Transport t;

    public Connectors(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/connectors} — List the caller's own community connector profiles (draft + published), with adoption counts. */
    public ListPage<Connector> list() {
        return t.get("/v1/connectors", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connectors} — Create a community connector profile (SEO profile + OAuth config + share modes). */
    public Connector create(ConnectorBody body) {
        return t.post("/v1/connectors", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public Connector create(ConnectorBody body, String idempotencyKey) {
        return t.post("/v1/connectors", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/connectors/{id}} — Fetch one of the caller's own connector profiles. */
    public Connector get(String id) {
        return t.get("/v1/connectors/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/connectors/{id}} — Edit a connector profile — SEO fields, OAuth config, and share modes. */
    public Connector update(String id, ConnectorBody body) {
        return t.patch("/v1/connectors/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/connectors/{id}} — Delete one of the caller's own connector profiles and cascade its installs, reviews, and reports. */
    public DeletedObject delete(String id) {
        return t.delete("/v1/connectors/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connectors/{id}/publish} — Publish a connector to the public directory. */
    public Connector publish(String id) {
        return t.post("/v1/connectors/" + enc(id) + "/publish", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connectors/{id}/unlist} — Remove a published connector from the directory (back to unlisted) without deleting it. */
    public Connector unlist(String id) {
        return t.post("/v1/connectors/" + enc(id) + "/unlist", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connectors/{id}/shared_credentials} — Set, rotate, or revoke the full_shared OAuth credentials. */
    public Connector setSharedCredentials(String id, ConnectorSharedCredentialsBody body) {
        return t.post("/v1/connectors/" + enc(id) + "/shared_credentials", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public Connector setSharedCredentials(String id, ConnectorSharedCredentialsBody body, String idempotencyKey) {
        return t.post("/v1/connectors/" + enc(id) + "/shared_credentials", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connectors/{slug}/install} — Enable a published connector on the caller's instance (prefill, full_shared, or full_idp), snapshotting its OAuth config into the connection. */
    public ConnectorInstall install(String slug, InstallConnectorBody body) {
        return t.post("/v1/connectors/" + enc(slug) + "/install", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectorInstall install(String slug, InstallConnectorBody body, String idempotencyKey) {
        return t.post("/v1/connectors/" + enc(slug) + "/install", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connectors/{slug}/reviews} — Rate a connector the caller installed (1–5, optional text). */
    public ConnectorReview review(String slug, Double rating, String body) {
        Map<String, Object> payload = Params.of().set("rating", rating).set("body", body).map();
        return t.post("/v1/connectors/" + enc(slug) + "/reviews", payload, new TypeReference<>() {
        });
    }

    public DataList<DirectoryEntry> directory() {
        return directory(null);
    }

    /** {@code GET /v1/directory} — Browse the unified connector directory — built-in providers plus published community connectors, ranked popular-first. */
    public DataList<DirectoryEntry> directory(DirectoryParams params) {
        return t.get("/v1/directory", params == null ? null : params.toQuery(), new TypeReference<>() {
        });
    }
}
