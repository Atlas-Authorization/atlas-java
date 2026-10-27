package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.IgaCampaignsCreateBody;
import net.atlasauth.atlas.models.IgaCampaignsDecideBody;
import net.atlasauth.atlas.models.IgaCampaignsDeleteResponse;
import net.atlasauth.atlas.models.IgaCampaignsEvidenceResponse;
import net.atlasauth.atlas.models.IgaCampaignsItemsResponse;
import net.atlasauth.atlas.models.IgaCampaignsItemsResponseDataItem;
import net.atlasauth.atlas.models.IgaCampaignsListResponse;
import net.atlasauth.atlas.models.IgaCampaignsListResponseDataItem;
import net.atlasauth.atlas.models.IgaCampaignsProgressResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/iga} resource. */
public final class IgaCampaigns {

    private final Transport t;

    public IgaCampaigns(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/iga/campaigns} - List certification (access review) campaigns. Scope: {@code iga:read}. */
    public IgaCampaignsListResponse list() {
        return list(null, null, null);
    }

    /** As above, with every optional parameter. */
    public IgaCampaignsListResponse list(String status, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("status", status).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/iga/campaigns", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/campaigns} - Create a draft campaign scoped to everything, specific entitlements, or the entitlements for an app, role, group or organization, with reviewers (the person's manager, the entitlement owner, or named users), a due date and reminder days. With `auto_revoke` the access reviewers mark for revocation is removed when the campaign closes. Scope: {@code iga:write}. */
    public IgaCampaignsListResponseDataItem create(IgaCampaignsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaCampaignsListResponseDataItem create(IgaCampaignsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/iga/campaigns", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/iga/campaigns/{id}} - Read a campaign with its progress. Scope: {@code iga:read}. */
    public IgaCampaignsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/iga/campaigns/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/iga/campaigns/{id}} - Delete a draft or closed campaign. Scope: {@code iga:write}. */
    public IgaCampaignsDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/iga/campaigns/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/campaigns/{id}/launch} - Launch a draft campaign: it adopts access that exists in Atlas but was never granted through governance, freezes the review list and notifies reviewers. Scope: {@code iga:write}. */
    public IgaCampaignsListResponseDataItem launch(String id) {
        return launch(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaCampaignsListResponseDataItem launch(String id, String idempotencyKey) {
        return t.request("POST", "/v1/iga/campaigns/" + enc(id) + "/launch", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/campaigns/{id}/close} - Close a campaign and apply its revoke decisions (with `auto_revoke`). Items nobody reviewed are left in place and reported as pending. Scope: {@code iga:write}. */
    public IgaCampaignsListResponseDataItem close(String id) {
        return close(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaCampaignsListResponseDataItem close(String id, String idempotencyKey) {
        return t.request("POST", "/v1/iga/campaigns/" + enc(id) + "/close", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/iga/campaigns/{id}/items} - The review items of a campaign. Scope: {@code iga:read}. */
    public IgaCampaignsItemsResponse items(String id) {
        return items(id, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public IgaCampaignsItemsResponse items(String id, String decision, String reviewerUserId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("decision", decision).set("reviewer_user_id", reviewerUserId).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/iga/campaigns/" + enc(id) + "/items", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/iga/campaigns/{id}/progress} - Progress: totals, percent complete, whether it is overdue, and per-reviewer counts. Scope: {@code iga:read}. */
    public IgaCampaignsProgressResponse progress(String id) {
        return t.request("GET", "/v1/iga/campaigns/" + enc(id) + "/progress", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/iga/campaigns/{id}/evidence} - The audit evidence for a campaign as JSON, or CSV with `format=csv`. People are identified by id only (an erased user is flagged), free-text comments are never included, and `masked_emails=true` adds an address reduced to its first letter and domain. Scope: {@code iga:read}. */
    public IgaCampaignsEvidenceResponse evidence(String id) {
        return evidence(id, null, null);
    }

    /** As above, with every optional parameter. */
    public IgaCampaignsEvidenceResponse evidence(String id, String format, String maskedEmails) {
        Map<String, Object> q = Params.of().set("format", format).set("masked_emails", maskedEmails).map();
        return t.request("GET", "/v1/iga/campaigns/" + enc(id) + "/evidence", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/iga/campaign_items/{id}/decide} - Certify or revoke one review item. With `reviewer_user_id` the decision is the reviewer's (they must be allowed to review it, and may not review their own access); without it it is an administrator decision. Scope: {@code iga:write}. */
    public IgaCampaignsItemsResponseDataItem decide(String id, IgaCampaignsDecideBody body) {
        return decide(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IgaCampaignsItemsResponseDataItem decide(String id, IgaCampaignsDecideBody body, String idempotencyKey) {
        return t.request("POST", "/v1/iga/campaign_items/" + enc(id) + "/decide", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
