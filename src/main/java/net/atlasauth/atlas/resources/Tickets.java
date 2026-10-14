package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateTicketBody;
import net.atlasauth.atlas.models.IssuedTicket;
import net.atlasauth.atlas.models.RedeemTicketBody;
import net.atlasauth.atlas.models.RedeemedTicket;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Signed, short-lived tickets ({@code /v1/tickets}) that carry a payload between your own services. */
public final class Tickets {

    private final Transport t;

    public Tickets(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/tickets} — Mint a one-time capability ticket: an opaque, high-entropy handle carrying an arbitrary JSON payload a holder can later redeem (e.g. */
    public IssuedTicket create(CreateTicketBody body) {
        return t.post("/v1/tickets", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public IssuedTicket create(CreateTicketBody body, String idempotencyKey) {
        return t.post("/v1/tickets", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/tickets/redeem} — Redeem a capability ticket server-to-server and return its payload. */
    public RedeemedTicket redeem(RedeemTicketBody body) {
        return t.post("/v1/tickets/redeem", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public RedeemedTicket redeem(RedeemTicketBody body, String idempotencyKey) {
        return t.post("/v1/tickets/redeem", body, idempotencyKey, new TypeReference<>() {
        });
    }
}
