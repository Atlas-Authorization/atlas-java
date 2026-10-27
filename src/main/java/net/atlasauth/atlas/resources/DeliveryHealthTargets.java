package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DeliveryHealthGetResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/delivery_health} resource. */
public final class DeliveryHealthTargets {

    private final Transport t;

    public DeliveryHealthTargets(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/delivery_health/{kind}/{id}} - Delivery health of one target. 404 until a delivery to it has been recorded. Scope: {@code webhooks:read}. */
    public DeliveryHealthGetResponse get(String kind, String id) {
        return t.request("GET", "/v1/delivery_health/" + enc(kind) + "/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }
}
