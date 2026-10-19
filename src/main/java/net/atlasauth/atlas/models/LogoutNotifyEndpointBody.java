package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The body of creating or updating a logout fan-out endpoint. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class LogoutNotifyEndpointBody {
    /** Public https endpoint (required on create). */
    public String url;
    /** A client_id to follow; omit for instance-wide (create only). */
    public String oauthClientId;
    /** The logout_token aud. Default: the client_id when following a client, else the instance id. */
    public String audience;
    /** Up to 200 characters. */
    public String description;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public LogoutNotifyEndpointBody() {
    }

    public LogoutNotifyEndpointBody url(String v) { this.url = v; return this; }
    public LogoutNotifyEndpointBody oauthClientId(String v) { this.oauthClientId = v; return this; }
    public LogoutNotifyEndpointBody audience(String v) { this.audience = v; return this; }
    public LogoutNotifyEndpointBody description(String v) { this.description = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public LogoutNotifyEndpointBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
