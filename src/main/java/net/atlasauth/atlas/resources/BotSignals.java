package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.BotExportPage;
import net.atlasauth.atlas.models.BotLabel;
import net.atlasauth.atlas.models.BotSignal;
import net.atlasauth.atlas.models.CreateBotLabelBody;

import java.util.Map;

/** The {@code /v1/bot_signals} + {@code /v1/bot_labels} export surface. */
public final class BotSignals {

    private final Transport t;

    public BotSignals(Transport t) {
        this.t = t;
    }

    /** Page the signal lake, newest first, for an incremental pull. */
    public BotExportPage<BotSignal> list() {
        return list(null, null);
    }

    /**
     * Page the signal lake, newest first.
     *
     * @param limit  1..500 (default 100).
     * @param before created-at epoch-ms cursor; returns rows OLDER than this.
     */
    public BotExportPage<BotSignal> list(Integer limit, Long before) {
        Map<String, Object> q = Params.of().set("limit", limit).set("before", before).map();
        return t.get("/v1/bot_signals", q, new TypeReference<>() {
        });
    }

    /** Page the training labels, newest first. */
    public BotExportPage<BotLabel> listLabels() {
        return listLabels(null, null);
    }

    public BotExportPage<BotLabel> listLabels(Integer limit, Long before) {
        Map<String, Object> q = Params.of().set("limit", limit).set("before", before).map();
        return t.get("/v1/bot_labels", q, new TypeReference<>() {
        });
    }

    /** Attach a training label to a user / device / ip_hash / attempt. */
    public BotLabel createLabel(CreateBotLabelBody body) {
        return createLabel(body, null);
    }

    public BotLabel createLabel(CreateBotLabelBody body, String idempotencyKey) {
        return t.post("/v1/bot_labels", body, idempotencyKey, new TypeReference<>() {
        });
    }
}
