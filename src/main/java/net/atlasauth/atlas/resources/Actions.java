package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.Action;
import net.atlasauth.atlas.models.ActionBindingList;
import net.atlasauth.atlas.models.ActionTestEvent;
import net.atlasauth.atlas.models.ActionTestResult;
import net.atlasauth.atlas.models.CreateActionBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.UpdateActionBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/actions} resource. */
public final class Actions {

    private final Transport t;

    public Actions(Transport t) {
        this.t = t;
    }

    public ListPage<Action> list() {
        return t.get("/v1/actions", new TypeReference<>() {
        });
    }

    public Action create(CreateActionBody body) {
        return create(body, null);
    }

    public Action create(CreateActionBody body, String idempotencyKey) {
        return t.post("/v1/actions", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public Action get(String id) {
        return t.get("/v1/actions/" + enc(id), new TypeReference<>() {
        });
    }

    public Action update(String id, UpdateActionBody body) {
        return t.patch("/v1/actions/" + enc(id), body, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/actions/" + enc(id), new TypeReference<>() {
        });
    }

    /** Dry-run against an empty sample event in the sandbox; nothing is persisted. */
    public ActionTestResult test(String id) {
        return test(id, new ActionTestEvent());
    }

    /** Dry-run against a sample event in the sandbox; nothing is persisted. */
    public ActionTestResult test(String id, ActionTestEvent event) {
        Map<String, Object> body = Params.of("event", event).map();
        return t.post("/v1/actions/" + enc(id) + "/test", body, new TypeReference<>() {
        });
    }

    /** The actions bound to a trigger, in run order. */
    public ActionBindingList getBindings(String trigger) {
        return t.get("/v1/actions/bindings/" + enc(trigger), new TypeReference<>() {
        });
    }

    /** Replace a trigger's ordered binding set. */
    public ActionBindingList setBindings(String trigger, List<String> actionIds) {
        Map<String, Object> body = Params.of("action_ids", actionIds).map();
        return t.put("/v1/actions/bindings/" + enc(trigger), body, new TypeReference<>() {
        });
    }
}
