package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AppInvitation;
import net.atlasauth.atlas.models.CreateAppInvitationBody;
import net.atlasauth.atlas.models.CursorPage;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * The top-level {@code /v1/invitations} resource — application invitations,
 * distinct from an organization's nested invitations.
 */
public final class Invitations {

    private final Transport t;

    public Invitations(Transport t) {
        this.t = t;
    }

    public CursorPage<AppInvitation> list() {
        return list(null, null);
    }

    public CursorPage<AppInvitation> list(Integer limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.get("/v1/invitations", q, new TypeReference<>() {
        });
    }

    public AppInvitation create(CreateAppInvitationBody body) {
        return create(body, null);
    }

    public AppInvitation create(CreateAppInvitationBody body, String idempotencyKey) {
        return t.post("/v1/invitations", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public AppInvitation revoke(String id) {
        return t.post("/v1/invitations/" + enc(id) + "/revoke", null, new TypeReference<>() {
        });
    }
}
