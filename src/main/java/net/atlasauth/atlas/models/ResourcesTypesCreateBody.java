package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/resource_types}. */
public final class ResourcesTypesCreateBody {
    public String key;
    public String name;
    public String description;
    public String parentType;
    public Boolean inherit;
    public List<Map<String, Object>> roles;

    public ResourcesTypesCreateBody() {
    }

    public ResourcesTypesCreateBody key(String v) { this.key = v; return this; }
    public ResourcesTypesCreateBody name(String v) { this.name = v; return this; }
    public ResourcesTypesCreateBody description(String v) { this.description = v; return this; }
    public ResourcesTypesCreateBody parentType(String v) { this.parentType = v; return this; }
    public ResourcesTypesCreateBody inherit(Boolean v) { this.inherit = v; return this; }
    public ResourcesTypesCreateBody roles(List<Map<String, Object>> v) { this.roles = v; return this; }
}
