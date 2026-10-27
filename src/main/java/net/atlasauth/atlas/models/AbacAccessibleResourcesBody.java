package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/abac/accessible_resources}. Required: principal, action, resourceType. */
public final class AbacAccessibleResourcesBody {
    /** Required. */
    public Map<String, Object> principal;
    /** Required. */
    public String action;
    /** The Atlas resource type key (the policy type is its PascalCase form). Required. */
    public String resourceType;
    public Map<String, Object> context;
    public List<Map<String, Object>> entities;
    public List<Map<String, Object>> candidates;
    public List<String> policyIds;
    /** Default 100. */
    public Long limit;
    public String startingAfter;

    public AbacAccessibleResourcesBody() {
    }

    public AbacAccessibleResourcesBody principal(Map<String, Object> v) { this.principal = v; return this; }
    public AbacAccessibleResourcesBody action(String v) { this.action = v; return this; }
    public AbacAccessibleResourcesBody resourceType(String v) { this.resourceType = v; return this; }
    public AbacAccessibleResourcesBody context(Map<String, Object> v) { this.context = v; return this; }
    public AbacAccessibleResourcesBody entities(List<Map<String, Object>> v) { this.entities = v; return this; }
    public AbacAccessibleResourcesBody candidates(List<Map<String, Object>> v) { this.candidates = v; return this; }
    public AbacAccessibleResourcesBody policyIds(List<String> v) { this.policyIds = v; return this; }
    public AbacAccessibleResourcesBody limit(Long v) { this.limit = v; return this; }
    public AbacAccessibleResourcesBody startingAfter(String v) { this.startingAfter = v; return this; }
}
