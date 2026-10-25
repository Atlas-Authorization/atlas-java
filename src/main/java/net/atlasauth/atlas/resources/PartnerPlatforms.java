package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.PartnerDelivery;
import net.atlasauth.atlas.models.PartnerPlatform;
import net.atlasauth.atlas.models.PartnerPlatformDeleted;
import net.atlasauth.atlas.models.PartnerPlatformList;
import net.atlasauth.atlas.models.PartnerPlatformRegistration;
import net.atlasauth.atlas.models.SetPartnerPlatformBody;
import net.atlasauth.atlas.models.StorePartnerPlatformTokenBody;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * {@code /v1/partner_platforms} — this instance as a partner platform of another product: a product that signs
 * its deliveries with a published Ed25519 key set and delivers signed consent events to the instance's receiver.
 * The partner registers from {@link #registration}; the partner token it hands back is stored once, encrypted,
 * and never appears in any response.
 */
public final class PartnerPlatforms {
    private final Transport t;

    public PartnerPlatforms(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/partner_platforms} — The partner platforms, with the ready-made presets. */
    public PartnerPlatformList list() {
        return t.get("/v1/partner_platforms", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/partner_platforms/{partner}} — One partner platform. */
    public PartnerPlatform get(String partner) {
        return t.get("/v1/partner_platforms/" + enc(partner), new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/partner_platforms/{partner}} — Set up or change a partner platform (a preset fills the defaults). */
    public PartnerPlatform set(String partner, SetPartnerPlatformBody body) {
        return t.put("/v1/partner_platforms/" + enc(partner), body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/partner_platforms/{partner}} — Remove the partner platform. */
    public PartnerPlatformDeleted remove(String partner) {
        return t.delete("/v1/partner_platforms/" + enc(partner), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/partner_platforms/{partner}/registration} — What to send the partner to be registered. */
    public PartnerPlatformRegistration registration(String partner) {
        return t.get("/v1/partner_platforms/" + enc(partner) + "/registration", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/partner_platforms/{partner}/token} — Store the token the partner handed over (once; {@code rotate} replaces it). The token is never returned. */
    public PartnerPlatform storeToken(String partner, StorePartnerPlatformTokenBody body) {
        return t.post("/v1/partner_platforms/" + enc(partner) + "/token", body, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/partner_platforms/{partner}/deliveries} — The recent deliveries the partner made. */
    public ListPage<PartnerDelivery> deliveries(String partner) {
        return deliveries(partner, null);
    }

    /** {@link #deliveries(String)} limited to {@code limit} entries. */
    public ListPage<PartnerDelivery> deliveries(String partner, Integer limit) {
        return t.get("/v1/partner_platforms/" + enc(partner) + "/deliveries", Params.of("limit", limit).map(), new TypeReference<>() {
        });
    }
}
