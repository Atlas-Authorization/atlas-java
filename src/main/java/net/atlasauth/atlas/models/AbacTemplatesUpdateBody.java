package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/abac_templates/{id}}. */
public final class AbacTemplatesUpdateBody {
    public String name;
    public String description;
    /** Policy source with `?principal` and/or `?resource` slots in the scope, e.g. `permit (principal == ?principal, action == Action::"read", resource in ?resource);`. */
    public String source;
    /** The FGA store `fga.check(...)` in the source consults. */
    public String fgaStoreId;

    public AbacTemplatesUpdateBody() {
    }

    public AbacTemplatesUpdateBody name(String v) { this.name = v; return this; }
    public AbacTemplatesUpdateBody description(String v) { this.description = v; return this; }
    public AbacTemplatesUpdateBody source(String v) { this.source = v; return this; }
    public AbacTemplatesUpdateBody fgaStoreId(String v) { this.fgaStoreId = v; return this; }
}
