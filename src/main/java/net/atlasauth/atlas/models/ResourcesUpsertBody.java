package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/resources}. */
public final class ResourcesUpsertBody {
    public String type;
    public String externalId;
    public String name;
    public Map<String, Object> parent;
    public Map<String, Object> metadata;

    public ResourcesUpsertBody() {
    }

    public ResourcesUpsertBody type(String v) { this.type = v; return this; }
    public ResourcesUpsertBody externalId(String v) { this.externalId = v; return this; }
    public ResourcesUpsertBody name(String v) { this.name = v; return this; }
    public ResourcesUpsertBody parent(Map<String, Object> v) { this.parent = v; return this; }
    public ResourcesUpsertBody metadata(Map<String, Object> v) { this.metadata = v; return this; }
}
