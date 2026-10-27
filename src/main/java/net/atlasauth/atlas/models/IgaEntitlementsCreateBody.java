package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/iga/entitlements}. Required: key, name, kind, target. */
public final class IgaEntitlementsCreateBody {
    /** Required. */
    public String key;
    /** Required. */
    public String name;
    public String description;
    /** Required. */
    public String kind;
    /** Required. */
    public Map<String, Object> target;
    public String ownerUserId;
    public String riskLevel;
    public Map<String, Object> approvalRules;
    public Long maxDurationSeconds;
    public Boolean requestable;
    public Boolean requireJustification;

    public IgaEntitlementsCreateBody() {
    }

    public IgaEntitlementsCreateBody key(String v) { this.key = v; return this; }
    public IgaEntitlementsCreateBody name(String v) { this.name = v; return this; }
    public IgaEntitlementsCreateBody description(String v) { this.description = v; return this; }
    public IgaEntitlementsCreateBody kind(String v) { this.kind = v; return this; }
    public IgaEntitlementsCreateBody target(Map<String, Object> v) { this.target = v; return this; }
    public IgaEntitlementsCreateBody ownerUserId(String v) { this.ownerUserId = v; return this; }
    public IgaEntitlementsCreateBody riskLevel(String v) { this.riskLevel = v; return this; }
    public IgaEntitlementsCreateBody approvalRules(Map<String, Object> v) { this.approvalRules = v; return this; }
    public IgaEntitlementsCreateBody maxDurationSeconds(Long v) { this.maxDurationSeconds = v; return this; }
    public IgaEntitlementsCreateBody requestable(Boolean v) { this.requestable = v; return this; }
    public IgaEntitlementsCreateBody requireJustification(Boolean v) { this.requireJustification = v; return this; }
}
