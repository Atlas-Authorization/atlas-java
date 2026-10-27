package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/iga/sod_rules}. Required: name, entitlementA, entitlementB. */
public final class IgaSodCreateRuleBody {
    /** Required. */
    public String name;
    public String description;
    /** Required. */
    public String entitlementA;
    /** Required. */
    public String entitlementB;
    public String mode;
    public Map<String, Object> approver;
    public Boolean enabled;

    public IgaSodCreateRuleBody() {
    }

    public IgaSodCreateRuleBody name(String v) { this.name = v; return this; }
    public IgaSodCreateRuleBody description(String v) { this.description = v; return this; }
    public IgaSodCreateRuleBody entitlementA(String v) { this.entitlementA = v; return this; }
    public IgaSodCreateRuleBody entitlementB(String v) { this.entitlementB = v; return this; }
    public IgaSodCreateRuleBody mode(String v) { this.mode = v; return this; }
    public IgaSodCreateRuleBody approver(Map<String, Object> v) { this.approver = v; return this; }
    public IgaSodCreateRuleBody enabled(Boolean v) { this.enabled = v; return this; }
}
