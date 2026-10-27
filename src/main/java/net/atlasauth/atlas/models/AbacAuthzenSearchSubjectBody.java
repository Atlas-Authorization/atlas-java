package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/authzen/access/v1/search/subject}. Required: subject, action, resource. */
public final class AbacAuthzenSearchSubjectBody {
    /** Required. */
    public Map<String, Object> subject;
    /** Required. */
    public Map<String, Object> action;
    /** Required. */
    public Map<String, Object> resource;
    public Map<String, Object> context;
    public Map<String, Object> page;

    public AbacAuthzenSearchSubjectBody() {
    }

    public AbacAuthzenSearchSubjectBody subject(Map<String, Object> v) { this.subject = v; return this; }
    public AbacAuthzenSearchSubjectBody action(Map<String, Object> v) { this.action = v; return this; }
    public AbacAuthzenSearchSubjectBody resource(Map<String, Object> v) { this.resource = v; return this; }
    public AbacAuthzenSearchSubjectBody context(Map<String, Object> v) { this.context = v; return this; }
    public AbacAuthzenSearchSubjectBody page(Map<String, Object> v) { this.page = v; return this; }
}
