package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/ssf/receivers}. */
public final class SsfReceiversCreateBody {
    public String name;
    public String issuer;
    public String jwksUri;
    public String audience;
    public Map<String, Object> actions;

    public SsfReceiversCreateBody() {
    }

    public SsfReceiversCreateBody name(String v) { this.name = v; return this; }
    public SsfReceiversCreateBody issuer(String v) { this.issuer = v; return this; }
    public SsfReceiversCreateBody jwksUri(String v) { this.jwksUri = v; return this; }
    public SsfReceiversCreateBody audience(String v) { this.audience = v; return this; }
    public SsfReceiversCreateBody actions(Map<String, Object> v) { this.actions = v; return this; }
}
