package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateWebhookEndpointBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.RotateWebhookSecretBody;
import net.atlasauth.atlas.models.UpdateWebhookEndpointBody;
import net.atlasauth.atlas.models.WebhookDelivery;
import net.atlasauth.atlas.models.WebhookDeliveryQueued;
import net.atlasauth.atlas.models.UpsertWebhookEndpointBody;
import net.atlasauth.atlas.models.WebhookBulkResult;
import net.atlasauth.atlas.models.WebhookEndpoint;
import net.atlasauth.atlas.models.WebhookEndpointList;
import net.atlasauth.atlas.models.WebhookEndpointWithSecret;
import net.atlasauth.atlas.models.WebhookSecret;
import net.atlasauth.atlas.models.WebhookTestResult;
import net.atlasauth.atlas.models.WebhookWindowBody;

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

    /**
     * {@code GET /v1/webhook_endpoints/{id}/deliveries} — The delivery log with every filter, newest first.
     * Each argument may be null: {@code limit} 1-1000 (server default 50), {@code startingAfter} (the
     * {@code next_cursor} of the previous page), {@code status} (pending, success, failed),
     * {@code eventType} (comma-separated event names), {@code since} / {@code until} (epoch ms).
     */
    public CursorPage<WebhookDelivery> deliveries(String id, Integer limit, String startingAfter, String status, String eventType, Long since, Long until) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter)
                .set("status", status).set("event_type", eventType).set("since", since).set("until", until).map();
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

        /** {@code GET /v1/webhook_endpoints/{id}} — One endpoint with its health. */
        public WebhookEndpoint get(String id) {
            return t.get("/v1/webhook_endpoints/" + enc(id), new TypeReference<>() {
            });
        }

        /**
         * {@code GET /v1/webhook_endpoints} — The endpoints plus the subscribable event types. With
         * {@code includeDeprecated} the deprecated event spellings are in the catalogue; {@code metadata}
         * keeps only endpoints whose provisioning tags match every pair. Both may be null.
         */
        public WebhookEndpointList listWithEvents(Boolean includeDeprecated, Map<String, Object> metadata) {
            Map<String, Object> q = Params.putMetadata(new java.util.LinkedHashMap<>(), metadata);
            if (includeDeprecated != null) {
                q.put("include_deprecated", includeDeprecated);
            }
            return t.get("/v1/webhook_endpoints", q, new TypeReference<>() {
            });
        }

        /**
         * {@code PUT /v1/webhook_endpoints/by_url} — Find the endpoint with this URL and update it, or create it
         * (then {@code secret} is set, once). Safe to run on every deploy.
         */
        public WebhookEndpointWithSecret upsertByUrl(UpsertWebhookEndpointBody body) {
            return upsertByUrl(body, null);
        }

        /** {@link #upsertByUrl(UpsertWebhookEndpointBody)} with an {@code Idempotency-Key}. */
        public WebhookEndpointWithSecret upsertByUrl(UpsertWebhookEndpointBody body, String idempotencyKey) {
            return t.request("PUT", "/v1/webhook_endpoints/by_url", null, body, idempotencyKey, false, new TypeReference<>() {
            });
        }

        /**
         * {@code POST /v1/webhook_endpoints/{id}/test?wait=} — Send {@code webhook.test} now and return the
         * result inline: status, HTTP status, latency, response snippet, error and the signature header sent.
         * {@code wait} is at most 10 seconds ({@code "10s"}, {@code "5000ms"}); null = {@code 10s}.
         */
        public WebhookTestResult testNow(String id, String wait) {
            Map<String, Object> q = Params.of("wait", wait != null ? wait : "10s").map();
            return t.request("POST", "/v1/webhook_endpoints/" + enc(id) + "/test", q, null, null, false, new TypeReference<>() {
            });
        }

        /**
         * {@code POST /v1/webhook_endpoints/{id}/redeliver} — Redeliver every event whose latest attempt in
         * the window ({@code since} / {@code until}, epoch ms) has the {@code status} (failed by default, or success).
         */
        public WebhookBulkResult redeliverWindow(String id, WebhookWindowBody body) {
            return redeliverWindow(id, body, null);
        }

        /** {@link #redeliverWindow(String, WebhookWindowBody)} with an {@code Idempotency-Key}. */
        public WebhookBulkResult redeliverWindow(String id, WebhookWindowBody body, String idempotencyKey) {
            return t.post("/v1/webhook_endpoints/" + enc(id) + "/redeliver", body, idempotencyKey, new TypeReference<>() {
            });
        }

        /**
         * {@code POST /v1/webhook_endpoints/{id}/replay} — Re-send everything since {@code since} that the
         * endpoint subscribes to and has not received ({@code eventType} limits it). When {@code hasMore},
         * continue from {@code nextSince}.
         */
        public WebhookBulkResult replay(String id, WebhookWindowBody body) {
            return replay(id, body, null);
        }

        /** {@link #replay(String, WebhookWindowBody)} with an {@code Idempotency-Key}. */
        public WebhookBulkResult replay(String id, WebhookWindowBody body, String idempotencyKey) {
            return t.post("/v1/webhook_endpoints/" + enc(id) + "/replay", body, idempotencyKey, new TypeReference<>() {
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
