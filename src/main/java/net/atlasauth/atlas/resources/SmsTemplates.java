package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateSmsTemplateBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.SmsTemplate;
import net.atlasauth.atlas.models.SmsTemplatePreview;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/sms_templates} resource. */
public final class SmsTemplates {

    private final Transport t;

    public SmsTemplates(Transport t) {
        this.t = t;
    }

    public ListPage<SmsTemplate> list() {
        return t.get("/v1/sms_templates", new TypeReference<>() {
        });
    }

    /** Upsert an override by name. */
    public SmsTemplate create(CreateSmsTemplateBody body) {
        return create(body, null);
    }

    public SmsTemplate create(CreateSmsTemplateBody body, String idempotencyKey) {
        return t.post("/v1/sms_templates", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public SmsTemplate get(String name) {
        return t.get("/v1/sms_templates/" + enc(name), new TypeReference<>() {
        });
    }

    public SmsTemplate update(String name, String body, Boolean enabled) {
        Map<String, Object> b = Params.of().set("body", body).set("enabled", enabled).map();
        return t.patch("/v1/sms_templates/" + enc(name), b, new TypeReference<>() {
        });
    }

    /** Revert to the built-in. */
    public SmsTemplate delete(String name) {
        return t.delete("/v1/sms_templates/" + enc(name), new TypeReference<>() {
        });
    }

    /** Render the stored/built-in body with no draft override. */
    public SmsTemplatePreview preview(String name) {
        return preview(name, null);
    }

    /** Render an unsaved draft (if a body is given) or the stored/built-in. */
    public SmsTemplatePreview preview(String name, String body) {
        Map<String, Object> b = Params.of().set("body", body).map();
        return t.post("/v1/sms_templates/" + enc(name) + "/preview", b, new TypeReference<>() {
        });
    }
}
