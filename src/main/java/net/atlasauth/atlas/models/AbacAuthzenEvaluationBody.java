package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/authzen/access/v1/evaluation}. */
public final class AbacAuthzenEvaluationBody {
    public Map<String, Object> subject;
    public Map<String, Object> resource;
    public Map<String, Object> action;
    public Map<String, Object> context;
    public List<Map<String, Object>> evaluations;
    public Map<String, Object> options;

    public AbacAuthzenEvaluationBody() {
    }

    public AbacAuthzenEvaluationBody subject(Map<String, Object> v) { this.subject = v; return this; }
    public AbacAuthzenEvaluationBody resource(Map<String, Object> v) { this.resource = v; return this; }
    public AbacAuthzenEvaluationBody action(Map<String, Object> v) { this.action = v; return this; }
    public AbacAuthzenEvaluationBody context(Map<String, Object> v) { this.context = v; return this; }
    public AbacAuthzenEvaluationBody evaluations(List<Map<String, Object>> v) { this.evaluations = v; return this; }
    public AbacAuthzenEvaluationBody options(Map<String, Object> v) { this.options = v; return this; }
}
