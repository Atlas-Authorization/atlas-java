package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/authzen/access/v1/search/action}. Required: subject, resource. */
public final class AbacAuthzenSearchActionBody {
    /** Required. */
    public Map<String, Object> subject;
    /** Required. */
    public Map<String, Object> resource;
    public Map<String, Object> context;
    public Map<String, Object> page;

    public AbacAuthzenSearchActionBody() {
    }

    public AbacAuthzenSearchActionBody subject(Map<String, Object> v) { this.subject = v; return this; }
    public AbacAuthzenSearchActionBody resource(Map<String, Object> v) { this.resource = v; return this; }
    public AbacAuthzenSearchActionBody context(Map<String, Object> v) { this.context = v; return this; }
    public AbacAuthzenSearchActionBody page(Map<String, Object> v) { this.page = v; return this; }
}
