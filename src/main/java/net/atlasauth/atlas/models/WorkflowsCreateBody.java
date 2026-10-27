package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/workflows}. */
public final class WorkflowsCreateBody {
    public String name;
    public String description;
    public Boolean enabled;
    public Map<String, Object> trigger;
    public List<Map<String, Object>> steps;

    public WorkflowsCreateBody() {
    }

    public WorkflowsCreateBody name(String v) { this.name = v; return this; }
    public WorkflowsCreateBody description(String v) { this.description = v; return this; }
    public WorkflowsCreateBody enabled(Boolean v) { this.enabled = v; return this; }
    public WorkflowsCreateBody trigger(Map<String, Object> v) { this.trigger = v; return this; }
    public WorkflowsCreateBody steps(List<Map<String, Object>> v) { this.steps = v; return this; }
}
