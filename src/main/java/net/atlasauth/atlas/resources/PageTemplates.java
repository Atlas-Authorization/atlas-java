package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.PageTemplatesListResponse;
import net.atlasauth.atlas.models.PageTemplatesListResponseDataItem;
import net.atlasauth.atlas.models.PageTemplatesPreviewResponse;
import net.atlasauth.atlas.models.PageTemplatesSaveDraftBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/instance} resource. */
public final class PageTemplates {

    private final Transport t;

    public PageTemplates(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/instance/page_templates} - The hosted-page templates of the instance: for each page (sign-in, sign-up, user-profile) the live template and the draft waiting to be previewed or promoted. Scope: {@code instance:read}. */
    public PageTemplatesListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public PageTemplatesListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/instance/page_templates", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/page_templates/{page}} - Read one page's live and draft template. Scope: {@code instance:read}. */
    public PageTemplatesListResponseDataItem get(String page) {
        return t.request("GET", "/v1/instance/page_templates/" + enc(page), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/instance/page_templates/{page}} - Save a page template as the page's draft. A template is data: components (heading, text, image, link, button_link, list, divider, spacer, columns) in four regions (header, above_form, below_form, footer). It is validated on save: no raw HTML, CSS or script, https and mailto links only. The live page is unchanged until you promote. Scope: {@code instance:write}. */
    public PageTemplatesListResponseDataItem saveDraft(String page, PageTemplatesSaveDraftBody body) {
        return t.request("PUT", "/v1/instance/page_templates/" + enc(page), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/page_templates/{page}} - Drop the draft or take the live template down (`status`). Scope: {@code instance:write}. */
    public PageTemplatesListResponseDataItem discard(String page) {
        return discard(page, null);
    }

    /** As above, with every optional parameter. */
    public PageTemplatesListResponseDataItem discard(String page, String status) {
        Map<String, Object> q = Params.of().set("status", status).map();
        return t.request("DELETE", "/v1/instance/page_templates/" + enc(page), q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/page_templates/{page}/preview} - A signed link to the hosted page with the draft template applied (valid for one hour, noindex, with a preview banner). 409 when the page has no template. Scope: {@code instance:read}. */
    public PageTemplatesPreviewResponse preview(String page) {
        return preview(page, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public PageTemplatesPreviewResponse preview(String page, String idempotencyKey) {
        return t.request("POST", "/v1/instance/page_templates/" + enc(page) + "/preview", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/page_templates/{page}/promote} - Make the draft the live template. Scope: {@code instance:write}. */
    public PageTemplatesListResponseDataItem promote(String page) {
        return promote(page, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public PageTemplatesListResponseDataItem promote(String page, String idempotencyKey) {
        return t.request("POST", "/v1/instance/page_templates/" + enc(page) + "/promote", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
