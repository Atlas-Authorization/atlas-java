package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/abac_schema/validate}. Required: source. */
public final class AbacSchemaValidateBody {
    /** Required. */
    public String source;
    public Map<String, Object> schema;
    public Boolean template;

    public AbacSchemaValidateBody() {
    }

    public AbacSchemaValidateBody source(String v) { this.source = v; return this; }
    public AbacSchemaValidateBody schema(Map<String, Object> v) { this.schema = v; return this; }
    public AbacSchemaValidateBody template(Boolean v) { this.template = v; return this; }
}
