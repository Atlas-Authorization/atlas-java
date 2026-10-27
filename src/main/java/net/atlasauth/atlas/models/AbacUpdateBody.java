package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/abac_policies/{id}}. */
public final class AbacUpdateBody {
    public String name;
    public String description;
    public Boolean enabled;
    public String fgaStoreId;
    public String source;
    public List<Map<String, Object>> tests;
    public Long currentVersion;

    public AbacUpdateBody() {
    }

    public AbacUpdateBody name(String v) { this.name = v; return this; }
    public AbacUpdateBody description(String v) { this.description = v; return this; }
    public AbacUpdateBody enabled(Boolean v) { this.enabled = v; return this; }
    public AbacUpdateBody fgaStoreId(String v) { this.fgaStoreId = v; return this; }
    public AbacUpdateBody source(String v) { this.source = v; return this; }
    public AbacUpdateBody tests(List<Map<String, Object>> v) { this.tests = v; return this; }
    public AbacUpdateBody currentVersion(Long v) { this.currentVersion = v; return this; }
}
