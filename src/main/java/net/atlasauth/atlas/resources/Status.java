package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * Atlas service status and the instance's status subscription: {@code GET /v1/status} (public), the
 * subscription ({@code /v1/instance/status_subscription}: regions, components, minimum impact, a private RSS /
 * Atom feed), the incidents that match it and the third-party status pages the instance depends on
 * ({@code /v1/instance/status_dependencies}). Components are frontend_api, backend_api, database, cache and
 * background_jobs. Incident webhooks: {@code status.incident.created}, {@code .updated}, {@code .resolved}.
 */
public final class Status {
    private final Transport t;

    public Status(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/status} — The current service status (public). */
    public JsonNode service() {
        return t.get("/v1/status", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/status_subscription} — The subscription. */
    public JsonNode getSubscription() {
        return t.get("/v1/instance/status_subscription", new TypeReference<>() {
        });
    }

    /**
     * {@code PUT /v1/instance/status_subscription} — Subscribe or change the subscription: {@code enabled},
     * {@code regions}, {@code component_ids}, {@code min_impact}, {@code include_maintenance},
     * {@code email_owners}, {@code webhook}. An empty list means all.
     */
    public JsonNode updateSubscription(Map<String, Object> body) {
        return t.put("/v1/instance/status_subscription", body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/status_subscription} — Turn the subscription off. */
    public JsonNode disableSubscription() {
        return t.delete("/v1/instance/status_subscription", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/status_subscription/rotate_feed} — New private feed URLs; the old ones stop working. */
    public JsonNode rotateFeed() {
        return t.post("/v1/instance/status_subscription/rotate_feed", null, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/status_incidents} — Incidents that match the subscription. */
    public JsonNode listIncidents() {
        return listIncidents(null, null);
    }

    /** {@code GET /v1/instance/status_incidents} — {@code open} true lists only unresolved incidents; {@code limit} caps the page. */
    public JsonNode listIncidents(Boolean open, Integer limit) {
        return t.get("/v1/instance/status_incidents", Params.of("open", open).set("limit", limit).map(), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/status_dependencies} — The third-party status pages the instance depends on. */
    public JsonNode listDependencies() {
        return t.get("/v1/instance/status_dependencies", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/status_dependencies} — Watch a third-party status page ({@code name}, {@code url}, optional {@code component_filter}, {@code enabled}). */
    public JsonNode createDependency(Map<String, Object> body) {
        return t.post("/v1/instance/status_dependencies", body, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/status_dependencies/{id}} — One dependency with its current state. */
    public JsonNode getDependency(String id) {
        return t.get("/v1/instance/status_dependencies/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/instance/status_dependencies/{id}} — Change a dependency. */
    public JsonNode updateDependency(String id, Map<String, Object> body) {
        return t.patch("/v1/instance/status_dependencies/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/status_dependencies/{id}} — Stop watching a dependency. */
    public JsonNode deleteDependency(String id) {
        return t.delete("/v1/instance/status_dependencies/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/status_dependencies/{id}/refresh} — Check the dependency now. */
    public JsonNode refreshDependency(String id) {
        return t.post("/v1/instance/status_dependencies/" + enc(id) + "/refresh", null, new TypeReference<>() {
        });
    }
}
