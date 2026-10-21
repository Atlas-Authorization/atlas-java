package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateWebhookEndpointBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.RotateWebhookSecretBody;
import net.atlasauth.atlas.models.UpdateWebhookEndpointBody;
import net.atlasauth.atlas.models.WebhookDelivery;
import net.atlasauth.atlas.models.WebhookDeliveryQueued;
import net.atlasauth.atlas.models.WebhookEndpoint;
import net.atlasauth.atlas.models.WebhookEndpointList;
import net.atlasauth.atlas.models.WebhookEndpointWithSecret;
import net.atlasauth.atlas.models.WebhookSecret;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/webhook_endpoints} resource, including its nested sub-resources. */
public final class Webhooks {

    private final Transport t;
    private final Endpoints endpoints;

    public Webhooks(Transport t) {
        this.t = t;
        this.endpoints = new Endpoints(t);
    }

    public Endpoints endpoints() {
        return endpoints;
    }

    /** Delivery log for an endpoint. */
    public ListPage<WebhookDelivery> deliveries(String id) {
        return t.get("/v1/webhook_endpoints/" + enc(id) + "/deliveries", new TypeReference<>() {
        });
    }

    /** Delivery log for an endpoint, newest first; {@code limit} defaults to 50 (max 100). */
    public ListPage<WebhookDelivery> deliveries(String id, Integer limit) {
        Map<String, Object> q = Params.of().set("limit", limit).map();
        return t.get("/v1/webhook_endpoints/" + enc(id) + "/deliveries", q, new TypeReference<>() {
        });
    }

    // ---- nested sub-resources ---------------------------------------------

    public static final class Endpoints {
        private final Transport t;

        Endpoints(Transport t) {
            this.t = t;
        }

        public ListPage<WebhookEndpoint> list() {
            return t.get("/v1/webhook_endpoints", new TypeReference<>() {
            });
        }

        /** The endpoints plus the subscribable event types and the retry schedule. */
        public WebhookEndpointList listWithEvents() {
            return t.get("/v1/webhook_endpoints", new TypeReference<>() {
            });
        }

        public WebhookEndpointWithSecret create(CreateWebhookEndpointBody body) {
            return create(body, null);
        }

        public WebhookEndpointWithSecret create(CreateWebhookEndpointBody body, String idempotencyKey) {
            return t.post("/v1/webhook_endpoints", body, idempotencyKey, new TypeReference<>() {
            });
        }

        public DeletedObject delete(String id) {
            return t.delete("/v1/webhook_endpoints/" + enc(id), new TypeReference<>() {
            });
        }

        /** {@code PATCH /v1/webhook_endpoints/{id}} — Update an endpoint’s url, enabled_events, or active flag in place. */
        public WebhookEndpoint update(String id, UpdateWebhookEndpointBody body) {
            return t.patch("/v1/webhook_endpoints/" + enc(id), body, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/webhook_endpoints/{id}/reveal_secret} — Show the endpoint's current signing secret again (for a verifier that lost it). */
        public WebhookSecret revealSecret(String id) {
            return t.post("/v1/webhook_endpoints/" + enc(id) + "/reveal_secret", null, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/webhook_endpoints/{id}/rotate_secret} — Rotate the endpoint signing secret. */
        public WebhookSecret rotateSecret(String id) {
            return t.post("/v1/webhook_endpoints/" + enc(id) + "/rotate_secret", null, new TypeReference<>() {
            });
        }

        /**
         * {@code POST /v1/webhook_endpoints/{id}/rotate_secret} — Rotate the signing secret with a
         * chosen overlap: for {@code overlapSeconds} (at most 604800) every delivery is signed with
         * both secrets, so a verifier holding either passes. Null or 0 stops the old one at once.
         */
        public WebhookSecret rotateSecret(String id, RotateWebhookSecretBody body) {
            return t.post("/v1/webhook_endpoints/" + enc(id) + "/rotate_secret", body, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/webhook_endpoints/{id}/test} — Send a synthetic webhook.test event to this endpoint only. */
        public WebhookDeliveryQueued test(String id) {
            return t.post("/v1/webhook_endpoints/" + enc(id) + "/test", null, new TypeReference<>() {
            });
        }
    }

    /** {@code POST /v1/webhook_endpoints/{id}/deliveries/{deliveryId}/redeliver} — Manually redeliver a past event as a fresh attempt. */
    public WebhookDeliveryQueued redeliver(String id, String deliveryId) {
        return t.post("/v1/webhook_endpoints/" + enc(id) + "/deliveries/" + enc(deliveryId) + "/redeliver", null, new TypeReference<>() {
        });
    }
}
