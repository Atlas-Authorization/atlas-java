package net.atlasauth.atlas.models;

import java.util.List;

/**
 * Body for creating ({@code url} required) or updating an organization's application webhook endpoint
 * ({@code /v1/organizations/{id}/app_webhook_endpoints}).
 */
public final class AppWebhookEndpointBody extends ExtensibleBody {
    public String url;
    /** Types from your catalog, or {@code ["*"]} (the default) for all of them. */
    public List<String> enabledEvents;
    public String description;
    /** false = switched off: nothing is delivered until it is enabled. Default true. */
    public Boolean active;

    public AppWebhookEndpointBody() {
    }

    public AppWebhookEndpointBody(String url) {
        this.url = url;
    }

    public AppWebhookEndpointBody url(String v) { this.url = v; return this; }
    public AppWebhookEndpointBody enabledEvents(List<String> v) { this.enabledEvents = v; return this; }
    public AppWebhookEndpointBody description(String v) { this.description = v; return this; }
    public AppWebhookEndpointBody active(Boolean v) { this.active = v; return this; }
    public AppWebhookEndpointBody set(String key, Object value) { putField(key, value); return this; }
}
