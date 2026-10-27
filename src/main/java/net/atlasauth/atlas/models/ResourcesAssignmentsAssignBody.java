package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/resource_role_assignments}. */
public final class ResourcesAssignmentsAssignBody {
    public Map<String, Object> resource;
    public Map<String, Object> subject;
    public String role;
    public Boolean inherit;

    public ResourcesAssignmentsAssignBody() {
    }

    public ResourcesAssignmentsAssignBody resource(Map<String, Object> v) { this.resource = v; return this; }
    public ResourcesAssignmentsAssignBody subject(Map<String, Object> v) { this.subject = v; return this; }
    public ResourcesAssignmentsAssignBody role(String v) { this.role = v; return this; }
    public ResourcesAssignmentsAssignBody inherit(Boolean v) { this.inherit = v; return this; }
}
