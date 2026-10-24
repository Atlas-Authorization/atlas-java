package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class UpdateWebhookEndpointBody extends WebhookEndpointSettingsBody<UpdateWebhookEndpointBody> {
    public String url;
    public List<String> enabledEvents;
    public Boolean active;

    public UpdateWebhookEndpointBody() {
    }

    @Override
    protected UpdateWebhookEndpointBody self() { return this; }

    public UpdateWebhookEndpointBody url(String v) { this.url = v; return this; }
    public UpdateWebhookEndpointBody enabledEvents(List<String> v) { this.enabledEvents = v; return this; }
    public UpdateWebhookEndpointBody active(Boolean v) { this.active = v; return this; }
}
