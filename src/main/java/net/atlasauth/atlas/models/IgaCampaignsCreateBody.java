package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/iga/campaigns}. Required: name. */
public final class IgaCampaignsCreateBody {
    /** Required. */
    public String name;
    public String description;
    public String scopeType;
    public List<String> scopeIds;
    public String reviewerType;
    public List<String> reviewerIds;
    public Long dueAt;
    public List<Long> reminderDays;
    public Boolean autoRevoke;

    public IgaCampaignsCreateBody() {
    }

    public IgaCampaignsCreateBody name(String v) { this.name = v; return this; }
    public IgaCampaignsCreateBody description(String v) { this.description = v; return this; }
    public IgaCampaignsCreateBody scopeType(String v) { this.scopeType = v; return this; }
    public IgaCampaignsCreateBody scopeIds(List<String> v) { this.scopeIds = v; return this; }
    public IgaCampaignsCreateBody reviewerType(String v) { this.reviewerType = v; return this; }
    public IgaCampaignsCreateBody reviewerIds(List<String> v) { this.reviewerIds = v; return this; }
    public IgaCampaignsCreateBody dueAt(Long v) { this.dueAt = v; return this; }
    public IgaCampaignsCreateBody reminderDays(List<Long> v) { this.reminderDays = v; return this; }
    public IgaCampaignsCreateBody autoRevoke(Boolean v) { this.autoRevoke = v; return this; }
}
