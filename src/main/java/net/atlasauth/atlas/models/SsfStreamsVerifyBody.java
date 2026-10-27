package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/ssf/streams/{id}/verify}. */
public final class SsfStreamsVerifyBody {
    public String state;

    public SsfStreamsVerifyBody() {
    }

    public SsfStreamsVerifyBody state(String v) { this.state = v; return this; }
}
