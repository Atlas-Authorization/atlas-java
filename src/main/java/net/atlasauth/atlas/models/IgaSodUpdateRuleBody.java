package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/iga/sod_rules/{id}}. */
public final class IgaSodUpdateRuleBody {
    public String name;
    public String description;
    public String mode;
    public Map<String, Object> approver;
    public Boolean enabled;

    public IgaSodUpdateRuleBody() {
    }

    public IgaSodUpdateRuleBody name(String v) { this.name = v; return this; }
    public IgaSodUpdateRuleBody description(String v) { this.description = v; return this; }
    public IgaSodUpdateRuleBody mode(String v) { this.mode = v; return this; }
    public IgaSodUpdateRuleBody approver(Map<String, Object> v) { this.approver = v; return this; }
    public IgaSodUpdateRuleBody enabled(Boolean v) { this.enabled = v; return this; }
}
