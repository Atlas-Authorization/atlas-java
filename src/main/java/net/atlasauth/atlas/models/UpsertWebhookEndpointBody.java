package net.atlasauth.atlas.models;

import java.util.List;

/**
 * Body for {@code PUT /v1/webhook_endpoints/by_url}: find the endpoint with this URL and update it, or
 * create it (then the response carries its secret, once). Safe to run on every deploy.
 */
public final class UpsertWebhookEndpointBody extends WebhookEndpointSettingsBody<UpsertWebhookEndpointBody> {
    public String url;
    public List<String> enabledEvents;
    public Boolean active;

    public UpsertWebhookEndpointBody() {
    }

    public UpsertWebhookEndpointBody(String url) {
        this.url = url;
    }

    @Override
    protected UpsertWebhookEndpointBody self() { return this; }

    public UpsertWebhookEndpointBody url(String v) { this.url = v; return this; }
    public UpsertWebhookEndpointBody enabledEvents(List<String> v) { this.enabledEvents = v; return this; }
    public UpsertWebhookEndpointBody active(Boolean v) { this.active = v; return this; }
}
