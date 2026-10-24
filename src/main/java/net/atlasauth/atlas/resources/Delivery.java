package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DeliveryHealth;
import net.atlasauth.atlas.models.DeliveryHealthList;
import net.atlasauth.atlas.models.EgressIps;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Outbound delivery health ({@code /v1/delivery_health}) across webhooks, the referral connection, back-channel receivers and integrations, and the addresses deliveries leave from. */
public final class Delivery {
    /** Delivery target kinds. */
    public static final String KIND_WEBHOOK_ENDPOINT = "webhook_endpoint";
    public static final String KIND_REFERRAL_CONNECTION = "referral_connection";
    public static final String KIND_BACKCHANNEL_RECEIVER = "backchannel_receiver";
    public static final String KIND_INTEGRATION = "integration";

    private final Transport t;

    public Delivery(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/meta/egress_ips} — The addresses deliveries leave from (public); allowlist them at your receiver. */
    public EgressIps egressIps() {
        return t.get("/v1/meta/egress_ips", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/delivery_health} — Every outbound target's health. */
    public DeliveryHealthList health() {
        return health(null, null);
    }

    /** {@code GET /v1/delivery_health} — {@code kind} is one of the {@code KIND_*} constants, {@code state} is {@code unhealthy} or {@code all}; both may be null. */
    public DeliveryHealthList health(String kind, String state) {
        return t.get("/v1/delivery_health", Params.of("kind", kind).set("state", state).map(), new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/delivery_health/{kind}/{targetId}/alert_contacts} — Who is emailed when a target fails (null = the account's owners). */
    public DeliveryHealth setAlertContacts(String kind, String targetId, List<String> alertContacts) {
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("alert_contacts", alertContacts);
        return t.put("/v1/delivery_health/" + enc(kind) + "/" + enc(targetId) + "/alert_contacts", body, new TypeReference<>() {
        });
    }
}
