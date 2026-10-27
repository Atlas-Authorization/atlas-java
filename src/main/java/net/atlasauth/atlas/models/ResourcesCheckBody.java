package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/resource_checks}. */
public final class ResourcesCheckBody {
    public Map<String, Object> subject;
    public String permission;
    public Map<String, Object> resource;
    public List<String> groups;

    public ResourcesCheckBody() {
    }

    public ResourcesCheckBody subject(Map<String, Object> v) { this.subject = v; return this; }
    public ResourcesCheckBody permission(String v) { this.permission = v; return this; }
    public ResourcesCheckBody resource(Map<String, Object> v) { this.resource = v; return this; }
    public ResourcesCheckBody groups(List<String> v) { this.groups = v; return this; }
}
