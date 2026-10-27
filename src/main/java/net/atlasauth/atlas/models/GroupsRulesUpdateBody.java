package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/group_rules/{id}}. */
public final class GroupsRulesUpdateBody {
    public String name;
    public Boolean enabled;
    /** Lower runs first (default 100). */
    public Long priority;
    /** A user-search filter tree (see POST /v1/users/search). */
    public Map<String, Object> condition;
    public List<Map<String, Object>> actions;

    public GroupsRulesUpdateBody() {
    }

    public GroupsRulesUpdateBody name(String v) { this.name = v; return this; }
    public GroupsRulesUpdateBody enabled(Boolean v) { this.enabled = v; return this; }
    public GroupsRulesUpdateBody priority(Long v) { this.priority = v; return this; }
    public GroupsRulesUpdateBody condition(Map<String, Object> v) { this.condition = v; return this; }
    public GroupsRulesUpdateBody actions(List<Map<String, Object>> v) { this.actions = v; return this; }
}
