package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/organizations/{id}/webhook_endpoints}. */
public final class OrganizationsWebhookEndpointsCreateBody {
    /** The HTTPS receiver (any public URL that answers 2xx). */
    public String url;
    /** Event types to receive, or ["*"]. Only events that belong to this organization are ever delivered. */
    public List<String> enabledEvents;
    /** A label, up to 500 characters. */
    public String description;
    /** Set false to pause deliveries. */
    public Boolean active;

    public OrganizationsWebhookEndpointsCreateBody() {
    }

    public OrganizationsWebhookEndpointsCreateBody url(String v) { this.url = v; return this; }
    public OrganizationsWebhookEndpointsCreateBody enabledEvents(List<String> v) { this.enabledEvents = v; return this; }
    public OrganizationsWebhookEndpointsCreateBody description(String v) { this.description = v; return this; }
    public OrganizationsWebhookEndpointsCreateBody active(Boolean v) { this.active = v; return this; }
}
