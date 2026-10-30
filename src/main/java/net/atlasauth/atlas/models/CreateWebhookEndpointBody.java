package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code POST /v1/webhook_endpoints}. */
public final class CreateWebhookEndpointBody extends WebhookEndpointSettingsBody<CreateWebhookEndpointBody> {
    /** Required unless {@code destination} is an event destination. */
    public String url;
    /** Each entry is {@code "*"} or a known event name. */
    public List<String> enabledEvents;
    /** False creates the endpoint switched off. Default true. */
    public Boolean active;

    public CreateWebhookEndpointBody() {
    }

    public CreateWebhookEndpointBody(String url) {
        this.url = url;
    }

    @Override
    protected CreateWebhookEndpointBody self() { return this; }

    public CreateWebhookEndpointBody url(String v) { this.url = v; return this; }
    public CreateWebhookEndpointBody enabledEvents(List<String> v) { this.enabledEvents = v; return this; }
    public CreateWebhookEndpointBody active(Boolean v) { this.active = v; return this; }
}
