package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/access_policies}. */
public final class AccessPoliciesCreateBody {
    public String name;
    public String description;
    public Boolean enabled;
    public Long priority;
    public List<String> stages;
    public Map<String, Object> conditions;
    public List<Map<String, Object>> grants;

    public AccessPoliciesCreateBody() {
    }

    public AccessPoliciesCreateBody name(String v) { this.name = v; return this; }
    public AccessPoliciesCreateBody description(String v) { this.description = v; return this; }
    public AccessPoliciesCreateBody enabled(Boolean v) { this.enabled = v; return this; }
    public AccessPoliciesCreateBody priority(Long v) { this.priority = v; return this; }
    public AccessPoliciesCreateBody stages(List<String> v) { this.stages = v; return this; }
    public AccessPoliciesCreateBody conditions(Map<String, Object> v) { this.conditions = v; return this; }
    public AccessPoliciesCreateBody grants(List<Map<String, Object>> v) { this.grants = v; return this; }
}
