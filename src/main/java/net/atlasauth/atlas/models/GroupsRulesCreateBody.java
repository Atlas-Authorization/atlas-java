package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/group_rules}. Required: name, condition, actions. */
public final class GroupsRulesCreateBody {
    /** Required. */
    public String name;
    public Boolean enabled;
    /** Lower runs first (default 100). */
    public Long priority;
    /** A user-search filter tree (see POST /v1/users/search). Required. */
    public Map<String, Object> condition;
    /** Required. */
    public List<Map<String, Object>> actions;

    public GroupsRulesCreateBody() {
    }

    public GroupsRulesCreateBody name(String v) { this.name = v; return this; }
    public GroupsRulesCreateBody enabled(Boolean v) { this.enabled = v; return this; }
    public GroupsRulesCreateBody priority(Long v) { this.priority = v; return this; }
    public GroupsRulesCreateBody condition(Map<String, Object> v) { this.condition = v; return this; }
    public GroupsRulesCreateBody actions(List<Map<String, Object>> v) { this.actions = v; return this; }
}
