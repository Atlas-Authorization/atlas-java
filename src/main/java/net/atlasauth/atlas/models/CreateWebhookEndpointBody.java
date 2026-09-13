package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code POST /v1/webhook_endpoints}. */
public final class CreateWebhookEndpointBody {
    public String url;
    /** Each entry is {@code "*"} or a known event name. */
    public List<String> enabledEvents;

    public CreateWebhookEndpointBody() {
    }

    public CreateWebhookEndpointBody(String url) {
        this.url = url;
    }

    public CreateWebhookEndpointBody url(String v) { this.url = v; return this; }
    public CreateWebhookEndpointBody enabledEvents(List<String> v) { this.enabledEvents = v; return this; }
}
