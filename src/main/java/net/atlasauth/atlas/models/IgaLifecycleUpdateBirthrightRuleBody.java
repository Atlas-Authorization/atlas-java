package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/iga/birthright_rules/{id}}. */
public final class IgaLifecycleUpdateBirthrightRuleBody {
    public String name;
    public Map<String, Object> match;
    public List<String> entitlementIds;
    public Boolean revokeOnUnmatch;
    public Boolean enabled;

    public IgaLifecycleUpdateBirthrightRuleBody() {
    }

    public IgaLifecycleUpdateBirthrightRuleBody name(String v) { this.name = v; return this; }
    public IgaLifecycleUpdateBirthrightRuleBody match(Map<String, Object> v) { this.match = v; return this; }
    public IgaLifecycleUpdateBirthrightRuleBody entitlementIds(List<String> v) { this.entitlementIds = v; return this; }
    public IgaLifecycleUpdateBirthrightRuleBody revokeOnUnmatch(Boolean v) { this.revokeOnUnmatch = v; return this; }
    public IgaLifecycleUpdateBirthrightRuleBody enabled(Boolean v) { this.enabled = v; return this; }
}
