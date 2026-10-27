package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/flows/{id}/activate}. Required: version. */
public final class JourneysActivateBody {
    /** Required. */
    public Long version;

    public JourneysActivateBody() {
    }

    public JourneysActivateBody version(Long v) { this.version = v; return this; }
}
