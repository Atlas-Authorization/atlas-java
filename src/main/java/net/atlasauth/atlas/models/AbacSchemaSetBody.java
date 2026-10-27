package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/abac_schema}. Required: schema. */
public final class AbacSchemaSetBody {
    /** Required. */
    public Map<String, Object> schema;

    public AbacSchemaSetBody() {
    }

    public AbacSchemaSetBody schema(Map<String, Object> v) { this.schema = v; return this; }
}
