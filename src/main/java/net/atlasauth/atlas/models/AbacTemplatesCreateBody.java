package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/abac_templates}. Required: name, source. */
public final class AbacTemplatesCreateBody {
    /** Required. */
    public String name;
    public String description;
    /** Policy source with `?principal` and/or `?resource` slots in the scope, e.g. `permit (principal == ?principal, action == Action::"read", resource in ?resource);`. Required. */
    public String source;
    /** The FGA store `fga.check(...)` in the source consults. */
    public String fgaStoreId;

    public AbacTemplatesCreateBody() {
    }

    public AbacTemplatesCreateBody name(String v) { this.name = v; return this; }
    public AbacTemplatesCreateBody description(String v) { this.description = v; return this; }
    public AbacTemplatesCreateBody source(String v) { this.source = v; return this; }
    public AbacTemplatesCreateBody fgaStoreId(String v) { this.fgaStoreId = v; return this; }
}
