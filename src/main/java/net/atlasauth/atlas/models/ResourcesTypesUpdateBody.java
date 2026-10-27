package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/resource_types/{key}}. */
public final class ResourcesTypesUpdateBody {
    public String name;
    public String description;
    public String parentType;
    public Boolean inherit;
    public List<Map<String, Object>> roles;

    public ResourcesTypesUpdateBody() {
    }

    public ResourcesTypesUpdateBody name(String v) { this.name = v; return this; }
    public ResourcesTypesUpdateBody description(String v) { this.description = v; return this; }
    public ResourcesTypesUpdateBody parentType(String v) { this.parentType = v; return this; }
    public ResourcesTypesUpdateBody inherit(Boolean v) { this.inherit = v; return this; }
    public ResourcesTypesUpdateBody roles(List<Map<String, Object>> v) { this.roles = v; return this; }
}
