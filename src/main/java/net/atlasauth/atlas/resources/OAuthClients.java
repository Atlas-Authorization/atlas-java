package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.BackchannelLogoutDelivery;
import net.atlasauth.atlas.models.BackchannelLogoutTest;
import net.atlasauth.atlas.models.CreateOAuthClientBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ImportOAuthClientsBody;
import net.atlasauth.atlas.models.ImportedOAuthClient;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.OAuthClient;
import net.atlasauth.atlas.models.OAuthClientGrant;
import net.atlasauth.atlas.models.OAuthClientWithSecret;
import net.atlasauth.atlas.models.RotateOAuthClientSecretBody;
import net.atlasauth.atlas.models.UpdateOAuthClientBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/oauth_clients} resource, including its nested {@code grants}. */
public final class OAuthClients {

    private final Transport t;
    private final Grants grants;

    public OAuthClients(Transport t) {
        this.t = t;
        this.grants = new Grants(t);
    }

    public ListPage<OAuthClient> list() {
        return t.get("/v1/oauth_clients", new TypeReference<>() {
        });
    }

    public OAuthClient get(String id) {
        return t.get("/v1/oauth_clients/" + enc(id), new TypeReference<>() {
        });
    }

    public OAuthClientWithSecret create(CreateOAuthClientBody body) {
        return create(body, null);
    }

    public OAuthClientWithSecret create(CreateOAuthClientBody body, String idempotencyKey) {
        return t.post("/v1/oauth_clients", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public OAuthClient update(String id, UpdateOAuthClientBody body) {
        return t.patch("/v1/oauth_clients/" + enc(id), body, new TypeReference<>() {
        });
    }

    /**
     * Rotate the client secret; the new secret is revealed exactly once. The previous secret keeps
     * working for the default overlap (24 hours); see {@link #rotateSecret(String, RotateOAuthClientSecretBody, String)}.
     */
    public OAuthClientWithSecret rotateSecret(String id) {
        return t.post("/v1/oauth_clients/" + enc(id) + "/rotate_secret", null, new TypeReference<>() {
        });
    }

    /**
     * Rotate the client secret with a chosen overlap: the previous secret keeps working for
     * {@code overlapSeconds} (default 86400, at most 604800; 0 revokes it now, e.g. after a leak).
     * {@link OAuthClientWithSecret#previousSecretExpiresAt()} says when it stops.
     *
     * @param idempotencyKey optional Idempotency-Key (null for none)
     */
    public OAuthClientWithSecret rotateSecret(String id, RotateOAuthClientSecretBody body, String idempotencyKey) {
        return t.post("/v1/oauth_clients/" + enc(id) + "/rotate_secret", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** Returns {@code {object,id,deleted,note}}. */
    public JsonNode delete(String id) {
        return t.delete("/v1/oauth_clients/" + enc(id), new TypeReference<>() {
        });
    }

    public CursorPage<BackchannelLogoutDelivery> listBackchannelDeliveries(String id) {
        return listBackchannelDeliveries(id, null, null);
    }

    /** {@code GET /v1/oauth_clients/{id}/backchannel_deliveries} — The client's back-channel logout delivery log, newest first. */
    public CursorPage<BackchannelLogoutDelivery> listBackchannelDeliveries(String id, Integer limit, String startingAfter) {
        return t.get("/v1/oauth_clients/" + enc(id) + "/backchannel_deliveries",
                Params.of().set("limit", limit).set("starting_after", startingAfter).map(), new TypeReference<>() {
                });
    }

    /** {@code POST /v1/oauth_clients/{id}/backchannel_logout/test} — Send a test back-channel logout now and return what the receiver answered. */
    public BackchannelLogoutTest testBackchannelLogout(String id) {
        return t.post("/v1/oauth_clients/" + enc(id) + "/backchannel_logout/test", null, new TypeReference<>() {
        });
    }

    public Grants grants() {
        return grants;
    }

    // ---- nested sub-resource ----------------------------------------------

    /** Client-to-resource-server grants under {@code /v1/oauth_clients/:id/grants}. */
    public static final class Grants {
        private final Transport t;

        Grants(Transport t) {
            this.t = t;
        }

        public ListPage<OAuthClientGrant> list(String clientId) {
            return t.get("/v1/oauth_clients/" + enc(clientId) + "/grants", new TypeReference<>() {
            });
        }

        public OAuthClientGrant create(String clientId, String resourceServerId, List<String> scopes) {
            Map<String, Object> body = Params.of()
                    .set("resource_server_id", resourceServerId)
                    .set("scopes", scopes)
                    .map();
            return t.post("/v1/oauth_clients/" + enc(clientId) + "/grants", body, new TypeReference<>() {
            });
        }

        public DeletedObject delete(String clientId, String grantId) {
            return t.delete("/v1/oauth_clients/" + enc(clientId) + "/grants/" + enc(grantId),
                    new TypeReference<>() {
                    });
        }
    }

    /** {@code POST /v1/oauth_clients/import} — Bulk-import OAuth clients for a migration, optionally with a pre-hashed client secret. */
    public ListPage<ImportedOAuthClient> importClients(ImportOAuthClientsBody body) {
        return t.post("/v1/oauth_clients/import", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public ListPage<ImportedOAuthClient> importClients(ImportOAuthClientsBody body, String idempotencyKey) {
        return t.post("/v1/oauth_clients/import", body, idempotencyKey, new TypeReference<>() {
        });
    }
}
