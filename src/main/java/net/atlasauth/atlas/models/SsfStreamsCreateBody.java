package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/ssf/streams}. */
public final class SsfStreamsCreateBody {
    public String deliveryMethod;
    public String endpointUrl;
    public String authorizationHeader;
    public String audience;
    public String description;
    public List<String> eventsRequested;

    public SsfStreamsCreateBody() {
    }

    public SsfStreamsCreateBody deliveryMethod(String v) { this.deliveryMethod = v; return this; }
    public SsfStreamsCreateBody endpointUrl(String v) { this.endpointUrl = v; return this; }
    public SsfStreamsCreateBody authorizationHeader(String v) { this.authorizationHeader = v; return this; }
    public SsfStreamsCreateBody audience(String v) { this.audience = v; return this; }
    public SsfStreamsCreateBody description(String v) { this.description = v; return this; }
    public SsfStreamsCreateBody eventsRequested(List<String> v) { this.eventsRequested = v; return this; }
}
