package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/feature_flags/evaluate}. */
public final class FlagsEvaluateBody {
    public String userId;
    public String orgId;
    public String plan;
    public List<String> keys;

    public FlagsEvaluateBody() {
    }

    public FlagsEvaluateBody userId(String v) { this.userId = v; return this; }
    public FlagsEvaluateBody orgId(String v) { this.orgId = v; return this; }
    public FlagsEvaluateBody plan(String v) { this.plan = v; return this; }
    public FlagsEvaluateBody keys(List<String> v) { this.keys = v; return this; }
}
