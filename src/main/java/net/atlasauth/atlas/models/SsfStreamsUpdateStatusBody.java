package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/ssf/streams/{id}/status}. */
public final class SsfStreamsUpdateStatusBody {
    public String status;

    public SsfStreamsUpdateStatusBody() {
    }

    public SsfStreamsUpdateStatusBody status(String v) { this.status = v; return this; }
}
