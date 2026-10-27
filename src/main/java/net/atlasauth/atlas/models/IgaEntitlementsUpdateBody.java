package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/iga/entitlements/{id}}. */
public final class IgaEntitlementsUpdateBody {
    public String key;
    public String name;
    public String description;
    public String kind;
    public Map<String, Object> target;
    public String ownerUserId;
    public String riskLevel;
    public Map<String, Object> approvalRules;
    public Long maxDurationSeconds;
    public Boolean requestable;
    public Boolean requireJustification;

    public IgaEntitlementsUpdateBody() {
    }

    public IgaEntitlementsUpdateBody key(String v) { this.key = v; return this; }
    public IgaEntitlementsUpdateBody name(String v) { this.name = v; return this; }
    public IgaEntitlementsUpdateBody description(String v) { this.description = v; return this; }
    public IgaEntitlementsUpdateBody kind(String v) { this.kind = v; return this; }
    public IgaEntitlementsUpdateBody target(Map<String, Object> v) { this.target = v; return this; }
    public IgaEntitlementsUpdateBody ownerUserId(String v) { this.ownerUserId = v; return this; }
    public IgaEntitlementsUpdateBody riskLevel(String v) { this.riskLevel = v; return this; }
    public IgaEntitlementsUpdateBody approvalRules(Map<String, Object> v) { this.approvalRules = v; return this; }
    public IgaEntitlementsUpdateBody maxDurationSeconds(Long v) { this.maxDurationSeconds = v; return this; }
    public IgaEntitlementsUpdateBody requestable(Boolean v) { this.requestable = v; return this; }
    public IgaEntitlementsUpdateBody requireJustification(Boolean v) { this.requireJustification = v; return this; }
}
