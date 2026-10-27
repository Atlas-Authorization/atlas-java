package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/migrations/{source}/imports}. */
public final class MigrationsImportsCreateBody {
    /** What the import does. Every field is optional; an unknown field is refused. A `better_auth` import takes the Better Auth options (see /v1/better_auth_imports); every other source takes these. */
    public Map<String, Object> options;

    public MigrationsImportsCreateBody() {
    }

    public MigrationsImportsCreateBody options(Map<String, Object> v) { this.options = v; return this; }
}
