package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.LogoutNotifyDelivery;
import net.atlasauth.atlas.models.LogoutNotifyEndpoint;
import net.atlasauth.atlas.models.LogoutNotifyEndpointBody;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * Logout fan-out ("also notify") endpoints ({@code /v1/logout_notify_endpoints}): downstream
 * services that receive the same signed {@code logout_token} as your OIDC apps whenever an Atlas
 * session ends, plus a subject-wide one (sub, no sid) on ban, deletion or deprovisioning.
 */
public final class LogoutNotifyEndpoints {

    private final Transport t;

    public LogoutNotifyEndpoints(Transport t) {
        this.t = t;
    }

    public ListPage<LogoutNotifyEndpoint> list() {
        return t.get("/v1/logout_notify_endpoints", new TypeReference<>() {
        });
    }

    /** Register an endpoint (public https). With {@code oauthClientId} it follows one client. */
    public LogoutNotifyEndpoint create(LogoutNotifyEndpointBody body) {
        return create(body, null);
    }

    public LogoutNotifyEndpoint create(LogoutNotifyEndpointBody body, String idempotencyKey) {
        return t.post("/v1/logout_notify_endpoints", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public LogoutNotifyEndpoint get(String id) {
        return t.get("/v1/logout_notify_endpoints/" + enc(id), new TypeReference<>() {
        });
    }

    /** Change the url, audience or description. */
    public LogoutNotifyEndpoint update(String id, LogoutNotifyEndpointBody body) {
        return t.patch("/v1/logout_notify_endpoints/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** Delete an endpoint; its queued deliveries go with it. */
    public DeletedObject delete(String id) {
        return t.delete("/v1/logout_notify_endpoints/" + enc(id), new TypeReference<>() {
        });
    }

    public ListPage<LogoutNotifyDelivery> listDeliveries(String id) {
        return listDeliveries(id, null);
    }

    /** The delivery log, newest first. {@code limit} 1-100, or null for the default. */
    public ListPage<LogoutNotifyDelivery> listDeliveries(String id, Integer limit) {
        return t.get("/v1/logout_notify_endpoints/" + enc(id) + "/deliveries", Params.of().set("limit", limit).map(),
                new TypeReference<>() {
                });
    }

    /** The delivery log, newest first, paged by cursor: pass the previous page's {@code next_cursor} as {@code startingAfter}. Each may be null. */
    public net.atlasauth.atlas.models.CursorPage<LogoutNotifyDelivery> listDeliveries(String id, Integer limit, String startingAfter) {
        return t.get("/v1/logout_notify_endpoints/" + enc(id) + "/deliveries", Params.of().set("limit", limit).set("starting_after", startingAfter).map(),
                new TypeReference<>() {
                });
    }
}
