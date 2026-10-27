package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/migrations/{source}/imports/{id}/apply}. */
public final class MigrationsImportsApplyBody {
    public Boolean confirmConflicts;

    public MigrationsImportsApplyBody() {
    }

    public MigrationsImportsApplyBody confirmConflicts(Boolean v) { this.confirmConflicts = v; return this; }
}
