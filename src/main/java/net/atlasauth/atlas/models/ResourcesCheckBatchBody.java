package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/resource_checks/batch}. */
public final class ResourcesCheckBatchBody {
    public List<Map<String, Object>> checks;

    public ResourcesCheckBatchBody() {
    }

    public ResourcesCheckBatchBody checks(List<Map<String, Object>> v) { this.checks = v; return this; }
}
