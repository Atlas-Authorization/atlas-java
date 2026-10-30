package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AppEvent;
import net.atlasauth.atlas.models.AppEventType;
import net.atlasauth.atlas.models.AppEventTypeBody;
import net.atlasauth.atlas.models.AppWebhookEndpointBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.OrganizationsWebhookEndpointsDeleteResponse;
import net.atlasauth.atlas.models.OrganizationsWebhookEndpointsListResponse;
import net.atlasauth.atlas.models.OrganizationsWebhookEndpointsListResponseDataItem;
import net.atlasauth.atlas.models.OrganizationsWebhookEndpointsRotateSecretResponse;
import net.atlasauth.atlas.models.PublishAppEventBody;
import net.atlasauth.atlas.models.WebhookDelivery;
import net.atlasauth.atlas.models.WebhookDeliveryQueued;
import net.atlasauth.atlas.models.WebhookSecret;
import net.atlasauth.atlas.models.WebhookWindowBody;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * Application webhooks: send your app's own events ({@code call.completed}, {@code invoice.paid}) to endpoints your
 * customers' organizations register. Atlas signs them with Standard Webhooks headers (verify with
 * {@link net.atlasauth.atlas.webhook.WebhookVerifier#verifyStandard}), retries, tracks health, switches off a dead
 * endpoint and keeps the delivery log with redeliver and replay.
 *
 * <pre>{@code
 * atlas.appEvents().types().create(new AppEventTypeBody("call.completed").description("A call finished."));
 * atlas.appEvents().endpoints().create("org_1", new AppWebhookEndpointBody("https://hooks.example.com/atlas"));
 * atlas.appEvents().publish(new PublishAppEventBody("org_1", "call.completed", Map.of("call_id", "c1")).idempotencyKey("c1"));
 * }</pre>
 */
public final class AppEvents {

    private final Transport t;
    private final Types types;
    private final Endpoints endpoints;

    public AppEvents(Transport t) {
        this.t = t;
        this.types = new Types(t);
        this.endpoints = new Endpoints(t);
    }

    /** Your event catalog ({@code /v1/app_event_types}). */
    public Types types() {
        return types;
    }

    /** One organization's endpoints for your events ({@code /v1/organizations/{id}/app_webhook_endpoints}). */
    public Endpoints endpoints() {
        return endpoints;
    }

    /**
     * {@code POST /v1/app_events} - publish one event to one organization's subscribed endpoints. A repeat with the
     * same idempotency key (in the body or the {@code Idempotency-Key} header; kept 90 days) returns the first event
     * with {@code idempotentReplay} true and queues nothing. Scope: {@code app_events:write}.
     */
    public AppEvent publish(PublishAppEventBody body) {
        return publish(body, null);
    }

    /** As above, with an {@code Idempotency-Key} header. */
    public AppEvent publish(PublishAppEventBody body, String idempotencyKey) {
        return t.request("POST", "/v1/app_events", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/app_events} - one organization's published events, newest first. */
    public CursorPage<AppEvent> list(String organizationId) {
        return list(organizationId, null, null, null);
    }

    /** As above, with every optional parameter. */
    public CursorPage<AppEvent> list(String organizationId, String type, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("organization_id", organizationId).set("type", type)
                .set("limit", limit).set("starting_after", startingAfter).map();
        return t.get("/v1/app_events", q, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/app_events/{id}} - one published event. */
    public AppEvent get(String id) {
        return t.get("/v1/app_events/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code /v1/app_event_types}. {@code idOrType} is the {@code aet_} id or the type name. */
    public static final class Types {
        private final Transport t;

        Types(Transport t) {
            this.t = t;
        }

        /** {@code GET /v1/app_event_types} - the whole catalog. */
        public ListPage<AppEventType> list() {
            return t.get("/v1/app_event_types", new TypeReference<>() {
            });
        }

        /** {@code POST /v1/app_event_types} - add a type ({@code type} required). */
        public AppEventType create(AppEventTypeBody body) {
            return create(body, null);
        }

        /** As above, with an {@code Idempotency-Key}. */
        public AppEventType create(AppEventTypeBody body, String idempotencyKey) {
            return t.post("/v1/app_event_types", body, idempotencyKey, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/app_event_types/{idOrType}}. */
        public AppEventType get(String idOrType) {
            return t.get("/v1/app_event_types/" + enc(idOrType), new TypeReference<>() {
            });
        }

        /** {@code PATCH /v1/app_event_types/{idOrType}} - description, schema, deprecated (a type cannot be renamed). */
        public AppEventType update(String idOrType, AppEventTypeBody body) {
            return t.patch("/v1/app_event_types/" + enc(idOrType), body, new TypeReference<>() {
            });
        }

        /** {@code DELETE /v1/app_event_types/{idOrType}}. */
        public DeletedObject delete(String idOrType) {
            return t.delete("/v1/app_event_types/" + enc(idOrType), new TypeReference<>() {
            });
        }
    }

    /**
     * {@code /v1/organizations/{id}/app_webhook_endpoints} - webhook endpoint objects as for an organization's
     * Atlas webhook endpoints. Organization members manage these from the frontend with {@code org:webhooks:manage}.
     */
    public static final class Endpoints {
        private final Transport t;

        Endpoints(Transport t) {
            this.t = t;
        }

        private static String base(String id) {
            return "/v1/organizations/" + enc(id) + "/app_webhook_endpoints";
        }

        /** {@code GET /v1/organizations/{id}/app_webhook_endpoints}. Never returns a secret. */
        public OrganizationsWebhookEndpointsListResponse list(String id) {
            return t.get(base(id), new TypeReference<>() {
            });
        }

        /** {@code POST /v1/organizations/{id}/app_webhook_endpoints}. The {@code whsec_} secret is in this response only (or {@link #revealSecret}). */
        public JsonNode create(String id, AppWebhookEndpointBody body) {
            return create(id, body, null);
        }

        /** As above, with an {@code Idempotency-Key}. */
        public JsonNode create(String id, AppWebhookEndpointBody body, String idempotencyKey) {
            return t.post(base(id), body, idempotencyKey, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/organizations/{id}/app_webhook_endpoints/{endpointId}}, with its delivery health. */
        public OrganizationsWebhookEndpointsListResponseDataItem get(String id, String endpointId) {
            return t.get(base(id) + "/" + enc(endpointId), new TypeReference<>() {
            });
        }

        /** {@code PATCH /v1/organizations/{id}/app_webhook_endpoints/{endpointId}}: url, enabled events, description, active. */
        public OrganizationsWebhookEndpointsListResponseDataItem update(String id, String endpointId, AppWebhookEndpointBody body) {
            return t.patch(base(id) + "/" + enc(endpointId), body, new TypeReference<>() {
            });
        }

        /** {@code DELETE /v1/organizations/{id}/app_webhook_endpoints/{endpointId}}. */
        public OrganizationsWebhookEndpointsDeleteResponse delete(String id, String endpointId) {
            return t.delete(base(id) + "/" + enc(endpointId), new TypeReference<>() {
            });
        }

        /** {@code POST .../{endpointId}/rotate_secret} - the previous secret keeps signing for 24 hours. */
        public OrganizationsWebhookEndpointsRotateSecretResponse rotateSecret(String id, String endpointId) {
            return t.post(base(id) + "/" + enc(endpointId) + "/rotate_secret", null, new TypeReference<>() {
            });
        }

        /** {@code POST .../{endpointId}/reveal_secret} - show the current signing secret again. */
        public WebhookSecret revealSecret(String id, String endpointId) {
            return t.post(base(id) + "/" + enc(endpointId) + "/reveal_secret", null, new TypeReference<>() {
            });
        }

        /** {@code POST .../{endpointId}/test} - queue a {@code webhook.test} event. */
        public JsonNode test(String id, String endpointId) {
            return test(id, endpointId, null);
        }

        /**
         * {@code POST .../{endpointId}/test?wait=} - with {@code wait} (e.g. {@code "10s"}) the attempt runs now and
         * its result comes back (200); without, it is queued (201).
         */
        public JsonNode test(String id, String endpointId, String wait) {
            Map<String, Object> q = Params.of().set("wait", wait).map();
            return t.request("POST", base(id) + "/" + enc(endpointId) + "/test", q, null, null, false, new TypeReference<>() {
            });
        }

        /** {@code GET .../{endpointId}/deliveries} - the delivery log, newest first. */
        public CursorPage<WebhookDelivery> deliveries(String id, String endpointId) {
            return deliveries(id, endpointId, null);
        }

        /** As above, with filters ({@code limit}, {@code starting_after}, {@code status}, {@code event_type}, {@code since}, {@code until}, {@code trace}). */
        public CursorPage<WebhookDelivery> deliveries(String id, String endpointId, Map<String, Object> query) {
            return t.get(base(id) + "/" + enc(endpointId) + "/deliveries", query, new TypeReference<>() {
            });
        }

        /** {@code POST .../{endpointId}/deliveries/{deliveryId}/redeliver} - re-send that delivery's event as a fresh attempt. */
        public WebhookDeliveryQueued redeliver(String id, String endpointId, String deliveryId) {
            return t.post(base(id) + "/" + enc(endpointId) + "/deliveries/" + enc(deliveryId) + "/redeliver", null, new TypeReference<>() {
            });
        }

        /**
         * {@code POST .../{endpointId}/replay} - re-send what the endpoint missed: {@code since} (epoch ms, or
         * {@code set("since", "failing_since")}), {@code until}, {@code eventType}. The endpoint must be active.
         */
        public JsonNode replay(String id, String endpointId, WebhookWindowBody body) {
            return t.post(base(id) + "/" + enc(endpointId) + "/replay", body, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/organizations/{id}/app_event_types} - the non-deprecated types an endpoint can subscribe to (the picker list). */
        public ListPage<AppEventType> appEventTypes(String id) {
            return t.get("/v1/organizations/" + enc(id) + "/app_event_types", new TypeReference<>() {
            });
        }
    }
}
