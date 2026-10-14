package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateNotificationTemplateBody;
import net.atlasauth.atlas.models.DataList;
import net.atlasauth.atlas.models.DeletedNotificationCategory;
import net.atlasauth.atlas.models.DeletedNotificationTemplate;
import net.atlasauth.atlas.models.NotificationCategory;
import net.atlasauth.atlas.models.NotificationSent;
import net.atlasauth.atlas.models.NotificationTemplate;
import net.atlasauth.atlas.models.SendNotificationBody;
import net.atlasauth.atlas.models.UpdateNotificationTemplateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Tenant-defined notifications: send one, and manage categories and templates. */
public final class Notifications {

    private final Transport t;
    private final Categories categories;
    private final Templates templates;

    public Notifications(Transport t) {
        this.t = t;
        this.categories = new Categories(t);
        this.templates = new Templates(t);
    }

    /** Notification categories ({@code /v1/notification_categories}). */
    public Categories categories() {
        return categories;
    }

    /** Notification templates ({@code /v1/notification_templates}). */
    public Templates templates() {
        return templates;
    }

    /** {@code POST /v1/notifications} — Send a templated, instance-branded security-notification email to one of your end-users, honouring their per-category notification preferences. */
    public NotificationSent send(SendNotificationBody body) {
        return t.post("/v1/notifications", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public NotificationSent send(SendNotificationBody body, String idempotencyKey) {
        return t.post("/v1/notifications", body, idempotencyKey, new TypeReference<>() {
        });
    }

    // ---- Categories --------------------------------------------------

    /** Notification categories ({@code /v1/notification_categories}). */
    public static final class Categories {
        private final Transport t;

        Categories(Transport t) {
            this.t = t;
        }

        /** {@code POST /v1/notification_categories} — Create a tenant-defined notification category: a key (unique per instance, cannot collide with a built-in) and the user-facing label shown on the end-user preference toggle. */
        public NotificationCategory create(String key, String label) {
            Map<String, Object> payload = Params.of().set("key", key).set("label", label).map();
            return t.post("/v1/notification_categories", payload, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/notification_categories} — List this instance's tenant-defined notification categories. */
        public DataList<NotificationCategory> list() {
            return t.get("/v1/notification_categories", new TypeReference<>() {
            });
        }

        /** {@code PUT /v1/notification_categories/{key}} — Update a tenant-defined notification category's user-facing label. */
        public NotificationCategory update(String key, String label) {
            Map<String, Object> payload = Params.of().set("label", label).map();
            return t.put("/v1/notification_categories/" + enc(key), payload, new TypeReference<>() {
            });
        }

        /** {@code DELETE /v1/notification_categories/{key}} — Delete a tenant-defined notification category. */
        public DeletedNotificationCategory delete(String key) {
            return t.delete("/v1/notification_categories/" + enc(key), new TypeReference<>() {
            });
        }
    }

    // ---- Templates ---------------------------------------------------

    /** Notification templates ({@code /v1/notification_templates}). */
    public static final class Templates {
        private final Transport t;

        Templates(Transport t) {
            this.t = t;
        }

        /** {@code POST /v1/notification_templates} — Create a tenant-defined notification template: a name (unique per instance), a subject and body with `{{variables}}`, and the category whose per-user preference gates it. */
        public NotificationTemplate create(CreateNotificationTemplateBody body) {
            return t.post("/v1/notification_templates", body, new TypeReference<>() {
            });
        }

        /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
        public NotificationTemplate create(CreateNotificationTemplateBody body, String idempotencyKey) {
            return t.post("/v1/notification_templates", body, idempotencyKey, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/notification_templates} — List this instance's tenant-defined notification templates. */
        public DataList<NotificationTemplate> list() {
            return t.get("/v1/notification_templates", new TypeReference<>() {
            });
        }

        /** {@code GET /v1/notification_templates/{name}} — Retrieve one tenant-defined notification template by name. */
        public NotificationTemplate get(String name) {
            return t.get("/v1/notification_templates/" + enc(name), new TypeReference<>() {
            });
        }

        /** {@code PUT /v1/notification_templates/{name}} — Update a tenant-defined notification template's subject, body and category by name. */
        public NotificationTemplate update(String name, UpdateNotificationTemplateBody body) {
            return t.put("/v1/notification_templates/" + enc(name), body, new TypeReference<>() {
            });
        }

        /** {@code DELETE /v1/notification_templates/{name}} — Delete a tenant-defined notification template by name. */
        public DeletedNotificationTemplate delete(String name) {
            return t.delete("/v1/notification_templates/" + enc(name), new TypeReference<>() {
            });
        }
    }
}
