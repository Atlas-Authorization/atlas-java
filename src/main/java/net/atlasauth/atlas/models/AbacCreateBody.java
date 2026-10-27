package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/abac_policies}. */
public final class AbacCreateBody {
    public String name;
    public String description;
    public Boolean enabled;
    public String fgaStoreId;
    public String source;
    public List<Map<String, Object>> tests;

    public AbacCreateBody() {
    }

    public AbacCreateBody name(String v) { this.name = v; return this; }
    public AbacCreateBody description(String v) { this.description = v; return this; }
    public AbacCreateBody enabled(Boolean v) { this.enabled = v; return this; }
    public AbacCreateBody fgaStoreId(String v) { this.fgaStoreId = v; return this; }
    public AbacCreateBody source(String v) { this.source = v; return this; }
    public AbacCreateBody tests(List<Map<String, Object>> v) { this.tests = v; return this; }
}
