package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/abac/diff}. Required: before, after. */
public final class AbacDiffSourcesBody {
    /** Required. */
    public String before;
    /** Required. */
    public String after;
    public Boolean semantic;
    public Long maxRequests;
    public Long maxChanges;

    public AbacDiffSourcesBody() {
    }

    public AbacDiffSourcesBody before(String v) { this.before = v; return this; }
    public AbacDiffSourcesBody after(String v) { this.after = v; return this; }
    public AbacDiffSourcesBody semantic(Boolean v) { this.semantic = v; return this; }
    public AbacDiffSourcesBody maxRequests(Long v) { this.maxRequests = v; return this; }
    public AbacDiffSourcesBody maxChanges(Long v) { this.maxChanges = v; return this; }
}
