package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/abac/partial_evaluate}. Required: principal, action, resource. */
public final class AbacPartialEvaluateBody {
    /** Required. */
    public Map<String, Object> principal;
    /** Required. */
    public String action;
    /** Required. */
    public Map<String, Object> resource;
    public Map<String, Object> context;
    /** Other entities the policies reference (hierarchy and attributes). */
    public List<Map<String, Object>> entities;
    /** Only these policies (default: every enabled one). */
    public List<String> policyIds;
    /** Which entity's attributes are unknown. Default ["resource"]. */
    public List<String> unknowns;
    /** Replace relationship conditions with the ids the FGA store returns. Default true. */
    public Boolean resolveRelations;
    public Map<String, Object> output;

    public AbacPartialEvaluateBody() {
    }

    public AbacPartialEvaluateBody principal(Map<String, Object> v) { this.principal = v; return this; }
    public AbacPartialEvaluateBody action(String v) { this.action = v; return this; }
    public AbacPartialEvaluateBody resource(Map<String, Object> v) { this.resource = v; return this; }
    public AbacPartialEvaluateBody context(Map<String, Object> v) { this.context = v; return this; }
    public AbacPartialEvaluateBody entities(List<Map<String, Object>> v) { this.entities = v; return this; }
    public AbacPartialEvaluateBody policyIds(List<String> v) { this.policyIds = v; return this; }
    public AbacPartialEvaluateBody unknowns(List<String> v) { this.unknowns = v; return this; }
    public AbacPartialEvaluateBody resolveRelations(Boolean v) { this.resolveRelations = v; return this; }
    public AbacPartialEvaluateBody output(Map<String, Object> v) { this.output = v; return this; }
}
