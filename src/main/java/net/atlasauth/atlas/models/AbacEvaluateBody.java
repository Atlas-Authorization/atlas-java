package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/abac/evaluate}. */
public final class AbacEvaluateBody {
    public Map<String, Object> principal;
    public String action;
    public Map<String, Object> resource;
    public Map<String, Object> context;
    public List<Map<String, Object>> entities;
    public List<String> policyIds;

    public AbacEvaluateBody() {
    }

    public AbacEvaluateBody principal(Map<String, Object> v) { this.principal = v; return this; }
    public AbacEvaluateBody action(String v) { this.action = v; return this; }
    public AbacEvaluateBody resource(Map<String, Object> v) { this.resource = v; return this; }
    public AbacEvaluateBody context(Map<String, Object> v) { this.context = v; return this; }
    public AbacEvaluateBody entities(List<Map<String, Object>> v) { this.entities = v; return this; }
    public AbacEvaluateBody policyIds(List<String> v) { this.policyIds = v; return this; }
}
