package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreatePartnerLinkBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.PartnerLink;
import net.atlasauth.atlas.models.SaveOutboundPartnerLinkBody;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * {@code /v1/partner_links} — credentials an integrated partner service holds on this instance (inbound),
 * and the ones this instance holds for a partner's API (outbound, {@link #outbound()}).
 */
public final class PartnerLinks {
    private final Transport t;
    private final Outbound outbound;

    public PartnerLinks(Transport t) {
        this.t = t;
        this.outbound = new Outbound(t);
    }

    /** The credentials this instance holds for partner APIs. */
    public Outbound outbound() {
        return outbound;
    }

    /** {@code POST /v1/partner_links} — Grant a partner a credential on this instance; the client secret is shown once. */
    public PartnerLink create(CreatePartnerLinkBody body) {
        return create(body, null);
    }

    /** {@link #create(CreatePartnerLinkBody)} with an {@code Idempotency-Key}. */
    public PartnerLink create(CreatePartnerLinkBody body, String idempotencyKey) {
        return t.post("/v1/partner_links", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/partner_links} — The partner links. */
    public ListPage<PartnerLink> list() {
        return t.get("/v1/partner_links", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/partner_links/{id}} — One partner link. */
    public PartnerLink get(String id) {
        return t.get("/v1/partner_links/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/partner_links/{id}/rotate_secret} — Issue a new client secret (shown once). */
    public PartnerLink rotateSecret(String id) {
        return t.post("/v1/partner_links/" + enc(id) + "/rotate_secret", null, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/partner_links/{id}} — Revoke a partner link. */
    public PartnerLink revoke(String id) {
        return t.delete("/v1/partner_links/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code /v1/partner_links/outbound} — credentials this instance holds for a partner's API (for example a data-rights orchestrator). */
    public static final class Outbound {
        private final Transport t;

        Outbound(Transport t) {
            this.t = t;
        }

        /** {@code GET /v1/partner_links/outbound} — The outbound links. */
        public JsonNode list() {
            return t.get("/v1/partner_links/outbound", new TypeReference<>() {
            });
        }

        /** {@code PUT /v1/partner_links/outbound/{partner}} — Create or replace the outbound link for a partner. */
        public JsonNode save(String partner, SaveOutboundPartnerLinkBody body) {
            return t.put("/v1/partner_links/outbound/" + enc(partner), body, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/partner_links/outbound/{partner}/test} — Call the partner with the stored credential. */
        public JsonNode test(String partner) {
            return t.post("/v1/partner_links/outbound/" + enc(partner) + "/test", null, new TypeReference<>() {
            });
        }

        /** {@code DELETE /v1/partner_links/outbound/{partner}} — Remove the outbound link. */
        public JsonNode remove(String partner) {
            return t.delete("/v1/partner_links/outbound/" + enc(partner), new TypeReference<>() {
            });
        }
    }
}
