package net.atlasauth.atlas.models;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/log_streams}. */
public final class CreateLogStreamBody {
    public String name;
    /** {@code http}, {@code datadog}, or {@code splunk}. */
    public String type;
    /** Shape depends on {@code type}: http {url,headers?}, datadog {site,api_key}, splunk {hec_endpoint,token}. */
    public Map<String, Object> destination;
    /** null (or omitted) forwards everything; an array of event-type prefixes filters. */
    public List<String> eventFilter;
    public Boolean enabled;

    public CreateLogStreamBody() {
    }

    public CreateLogStreamBody(String name, String type, Map<String, Object> destination) {
        this.name = name;
        this.type = type;
        this.destination = destination;
    }

    public CreateLogStreamBody name(String v) { this.name = v; return this; }
    public CreateLogStreamBody type(String v) { this.type = v; return this; }
    public CreateLogStreamBody destination(Map<String, Object> v) { this.destination = v; return this; }
    public CreateLogStreamBody eventFilter(List<String> v) { this.eventFilter = v; return this; }
    public CreateLogStreamBody enabled(Boolean v) { this.enabled = v; return this; }
}
