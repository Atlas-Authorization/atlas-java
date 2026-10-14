package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DataList;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.WaitlistEntry;
import net.atlasauth.atlas.models.WaitlistListResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/waitlist_entries} resource. */
public final class Waitlist {

    private final Transport t;

    public Waitlist(Transport t) {
        this.t = t;
    }

    public WaitlistListResponse list() {
        return list(null, null, null, null);
    }

    public WaitlistListResponse list(Integer limit, String startingAfter, String status, String query) {
        Map<String, Object> q = Params.of()
                .set("limit", limit)
                .set("starting_after", startingAfter)
                .set("status", status)
                .set("query", query)
                .map();
        return t.get("/v1/waitlist_entries", q, new TypeReference<>() {
        });
    }

    /**
     * Approve or deny a waitlist entry. {@code status} is {@code "approved"} or
     * {@code "denied"}; {@code note} is optional.
     */
    public WaitlistEntry decide(String id, String status, String note) {
        Map<String, Object> body = Params.of().set("status", status).set("note", note).map();
        return t.post("/v1/waitlist_entries/" + enc(id) + "/decide", body, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/waitlist_entries} — Admin-create a waitlist entry (idempotent on address). */
    public WaitlistEntry create(String emailAddress) {
        Map<String, Object> payload = Params.of().set("email_address", emailAddress).map();
        return t.post("/v1/waitlist_entries", payload, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/waitlist_entries/bulk} — Bulk-create waitlist entries (≤100 addresses); per-entry errors returned. */
    public DataList<WaitlistEntry> createBulk(List<String> emailAddresses) {
        Map<String, Object> payload = Params.of().set("email_addresses", emailAddresses).map();
        return t.post("/v1/waitlist_entries/bulk", payload, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/waitlist_entries/{id}} — Delete a waitlist entry. */
    public DeletedObject delete(String id) {
        return t.delete("/v1/waitlist_entries/" + enc(id), new TypeReference<>() {
        });
    }
}
