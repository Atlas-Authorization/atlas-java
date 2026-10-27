package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/resource_checks/list}. */
public final class ResourcesAccessibleBody {
    public Map<String, Object> subject;
    public String permission;
    public String type;
    public List<String> groups;
    public Long limit;
    public String startingAfter;

    public ResourcesAccessibleBody() {
    }

    public ResourcesAccessibleBody subject(Map<String, Object> v) { this.subject = v; return this; }
    public ResourcesAccessibleBody permission(String v) { this.permission = v; return this; }
    public ResourcesAccessibleBody type(String v) { this.type = v; return this; }
    public ResourcesAccessibleBody groups(List<String> v) { this.groups = v; return this; }
    public ResourcesAccessibleBody limit(Long v) { this.limit = v; return this; }
    public ResourcesAccessibleBody startingAfter(String v) { this.startingAfter = v; return this; }
}
