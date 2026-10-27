package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/abac_policies/{id}/test}. */
public final class AbacTestBody {
    public Long version;
    public String source;
    public List<Map<String, Object>> tests;

    public AbacTestBody() {
    }

    public AbacTestBody version(Long v) { this.version = v; return this; }
    public AbacTestBody source(String v) { this.source = v; return this; }
    public AbacTestBody tests(List<Map<String, Object>> v) { this.tests = v; return this; }
}
