package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateWebhookEndpointBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.WebhookDelivery;
import net.atlasauth.atlas.models.WebhookEndpoint;
import net.atlasauth.atlas.models.WebhookEndpointWithSecret;

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
    }
}
