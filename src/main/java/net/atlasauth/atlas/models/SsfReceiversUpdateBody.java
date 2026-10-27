package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/ssf/receivers/{id}}. */
public final class SsfReceiversUpdateBody {
    public String name;
    public String jwksUri;
    public String audience;
    public Map<String, Object> actions;
    public Boolean enabled;

    public SsfReceiversUpdateBody() {
    }

    public SsfReceiversUpdateBody name(String v) { this.name = v; return this; }
    public SsfReceiversUpdateBody jwksUri(String v) { this.jwksUri = v; return this; }
    public SsfReceiversUpdateBody audience(String v) { this.audience = v; return this; }
    public SsfReceiversUpdateBody actions(Map<String, Object> v) { this.actions = v; return this; }
    public SsfReceiversUpdateBody enabled(Boolean v) { this.enabled = v; return this; }
}
