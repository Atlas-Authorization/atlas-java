package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class UpdateWebhookEndpointBody {
    public String url;
    public List<String> enabledEvents;
    public Boolean active;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateWebhookEndpointBody() {
    }

    public UpdateWebhookEndpointBody url(String v) { this.url = v; return this; }
    public UpdateWebhookEndpointBody enabledEvents(List<String> v) { this.enabledEvents = v; return this; }
    public UpdateWebhookEndpointBody active(Boolean v) { this.active = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public UpdateWebhookEndpointBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
