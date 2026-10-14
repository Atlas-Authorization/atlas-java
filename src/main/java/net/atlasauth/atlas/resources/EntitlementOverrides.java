package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateEntitlementOverrideBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.EntitlementOverride;
import net.atlasauth.atlas.models.Grant;
import net.atlasauth.atlas.models.ListPage;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Sales-led entitlement overrides ({@code /v1/entitlement_overrides}). */
public final class EntitlementOverrides {

    private final Transport t;

    public EntitlementOverrides(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/entitlement_overrides} — Grant a SALES-LED entitlement override (P1-6): pin a plan / features / limits onto a user or org without Stripe (e.g. */
    public EntitlementOverride create(CreateEntitlementOverrideBody body) {
        return t.post("/v1/entitlement_overrides", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public EntitlementOverride create(CreateEntitlementOverrideBody body, String idempotencyKey) {
        return t.post("/v1/entitlement_overrides", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public ListPage<EntitlementOverride> list() {
        return list(null, null);
    }

    /** {@code GET /v1/entitlement_overrides} — List sales-led entitlement overrides, optionally filtered by subject_type (user/org) and subject_id. */
    public ListPage<EntitlementOverride> list(String subjectType, String subjectId) {
        Map<String, Object> q = Params.of().set("subject_type", subjectType).set("subject_id", subjectId).map();
        return t.get("/v1/entitlement_overrides", q, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/entitlement_overrides/{id}} — Revoke (hard-delete) a sales-led entitlement override. */
    public DeletedObject delete(String id) {
        return t.delete("/v1/entitlement_overrides/" + enc(id), new TypeReference<>() {
        });
    }
}
