package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/resources/sync}. */
public final class ResourcesSyncBody {
    public List<Map<String, Object>> resources;

    public ResourcesSyncBody() {
    }

    public ResourcesSyncBody resources(List<Map<String, Object>> v) { this.resources = v; return this; }
}
