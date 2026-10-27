package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/ssf/streams/{id}}. */
public final class SsfStreamsUpdateBody {
    public String endpointUrl;
    public String authorizationHeader;
    public String description;
    public List<String> eventsRequested;
    public String status;

    public SsfStreamsUpdateBody() {
    }

    public SsfStreamsUpdateBody endpointUrl(String v) { this.endpointUrl = v; return this; }
    public SsfStreamsUpdateBody authorizationHeader(String v) { this.authorizationHeader = v; return this; }
    public SsfStreamsUpdateBody description(String v) { this.description = v; return this; }
    public SsfStreamsUpdateBody eventsRequested(List<String> v) { this.eventsRequested = v; return this; }
    public SsfStreamsUpdateBody status(String v) { this.status = v; return this; }
}
