package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/iga/campaign_items/{id}/decide}. Required: decision. */
public final class IgaCampaignsDecideBody {
    /** Required. */
    public String decision;
    public String comment;
    public String reviewerUserId;

    public IgaCampaignsDecideBody() {
    }

    public IgaCampaignsDecideBody decision(String v) { this.decision = v; return this; }
    public IgaCampaignsDecideBody comment(String v) { this.comment = v; return this; }
    public IgaCampaignsDecideBody reviewerUserId(String v) { this.reviewerUserId = v; return this; }
}
