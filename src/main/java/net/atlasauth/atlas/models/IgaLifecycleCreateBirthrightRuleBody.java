package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/iga/birthright_rules}. Required: name, match, entitlementIds. */
public final class IgaLifecycleCreateBirthrightRuleBody {
    /** Required. */
    public String name;
    /** Required. */
    public Map<String, Object> match;
    /** Required. */
    public List<String> entitlementIds;
    public Boolean revokeOnUnmatch;
    public Boolean enabled;

    public IgaLifecycleCreateBirthrightRuleBody() {
    }

    public IgaLifecycleCreateBirthrightRuleBody name(String v) { this.name = v; return this; }
    public IgaLifecycleCreateBirthrightRuleBody match(Map<String, Object> v) { this.match = v; return this; }
    public IgaLifecycleCreateBirthrightRuleBody entitlementIds(List<String> v) { this.entitlementIds = v; return this; }
    public IgaLifecycleCreateBirthrightRuleBody revokeOnUnmatch(Boolean v) { this.revokeOnUnmatch = v; return this; }
    public IgaLifecycleCreateBirthrightRuleBody enabled(Boolean v) { this.enabled = v; return this; }
}
