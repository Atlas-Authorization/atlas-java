package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateMachineBody;
import net.atlasauth.atlas.models.CreateMachineEnrolmentTokenBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListMachineEnrolmentTokensParams;
import net.atlasauth.atlas.models.ListMachinesParams;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.M2mTokenVerification;
import net.atlasauth.atlas.models.Machine;
import net.atlasauth.atlas.models.MachineEnrolmentToken;
import net.atlasauth.atlas.models.MachineEnrolmentTokenWithSecret;
import net.atlasauth.atlas.models.MachineWithSecret;
import net.atlasauth.atlas.models.UpdateMachineMetadataBody;
import net.atlasauth.atlas.models.UserOrganizationMembership;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Machine identities ({@code /v1/machines}), machine-token verification and enrolment tokens. */
public final class Machines {

    private final Transport t;
    private final EnrolmentTokens enrolmentTokens;

    public Machines(Transport t) {
        this.t = t;
        this.enrolmentTokens = new EnrolmentTokens(t);
    }

    /** Machine enrolment tokens ({@code /v1/machine_enrolment_tokens}). */
    public EnrolmentTokens enrolmentTokens() {
        return enrolmentTokens;
    }

    /** {@code POST /v1/machines} — Create a named machine (M2M) identity; returns the machine secret (mk_) ONCE. */
    public MachineWithSecret create(CreateMachineBody body) {
        return t.post("/v1/machines", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public MachineWithSecret create(CreateMachineBody body, String idempotencyKey) {
        return t.post("/v1/machines", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public CursorPage<Machine> list() {
        return list(null);
    }

    /** {@code GET /v1/machines} — List machine identities (device registry), filtered by lifecycle status, reported hostname, owning user, organization, and/or metadata, with keyset pagination. */
    public CursorPage<Machine> list(ListMachinesParams params) {
        return t.get("/v1/machines", params == null ? null : params.toQuery(), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/machines/{id}} — Fetch one named machine identity by id. */
    public Machine get(String id) {
        return t.get("/v1/machines/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/machines/{id}} — Rename a machine identity. */
    public Machine update(String id, String name) {
        Map<String, Object> payload = Params.of().set("name", name).map();
        return t.patch("/v1/machines/" + enc(id), payload, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/machines/{id}} — Delete a machine identity. */
    public DeletedObject delete(String id) {
        return t.delete("/v1/machines/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/machines/{id}/organization_memberships} — List the organizations a machine belongs to with the org's resolved plan and the enrolment time — the machine-token counterpart of the user organization_memberships route. */
    public ListPage<UserOrganizationMembership> listOrganizationMemberships(String id) {
        return t.get("/v1/machines/" + enc(id) + "/organization_memberships", new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/machines/{id}/metadata} — Merge device metadata (hostname, os, app_version, last_seen_at) into a machine. */
    public Machine updateMetadata(String id, UpdateMachineMetadataBody body) {
        return t.patch("/v1/machines/" + enc(id) + "/metadata", body, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/machines/{id}/approve} — Approve a device that enrolled under an approval-gated token (pending → active). */
    public Machine approve(String id) {
        return t.post("/v1/machines/" + enc(id) + "/approve", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/machines/{id}/revoke} — Revoke a machine device; its key stops minting tokens immediately (record kept). */
    public Machine revoke(String id) {
        return t.post("/v1/machines/" + enc(id) + "/revoke", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/machines/{id}/rotate_secret} — Rotate a machine secret; the old one stops working and a new mk_ is shown once. */
    public MachineWithSecret rotateSecret(String id) {
        return t.post("/v1/machines/" + enc(id) + "/rotate_secret", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/machines/{id}/rotate_key} — Rotate a device keypair: register a new public key; the old private key stops verifying. */
    public Machine rotateKey(String id, String publicKeyPem) {
        Map<String, Object> payload = Params.of().set("public_key_pem", publicKeyPem).map();
        return t.post("/v1/machines/" + enc(id) + "/rotate_key", payload, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/m2m_tokens/verify} — Verify a presented machine secret → { valid, machine_id? */
    public M2mTokenVerification verifyToken(String token) {
        Map<String, Object> payload = Params.of().set("token", token).map();
        return t.post("/v1/m2m_tokens/verify", payload, new TypeReference<>() {
        });
    }

    // ---- EnrolmentTokens ---------------------------------------------

    /** Machine enrolment tokens ({@code /v1/machine_enrolment_tokens}). */
    public static final class EnrolmentTokens {
        private final Transport t;

        EnrolmentTokens(Transport t) {
            this.t = t;
        }

        /** {@code POST /v1/machine_enrolment_tokens} — Mint a machine enrolment token (single/multi-use, expiring, org-bound, approval-gated). */
        public MachineEnrolmentTokenWithSecret create(CreateMachineEnrolmentTokenBody body) {
            return t.post("/v1/machine_enrolment_tokens", body, new TypeReference<>() {
            });
        }

        /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
        public MachineEnrolmentTokenWithSecret create(CreateMachineEnrolmentTokenBody body, String idempotencyKey) {
            return t.post("/v1/machine_enrolment_tokens", body, idempotencyKey, new TypeReference<>() {
            });
        }

        public CursorPage<MachineEnrolmentToken> list() {
            return list(null);
        }

        /** {@code GET /v1/machine_enrolment_tokens} — List machine enrolment tokens (never the secret), newest first. */
        public CursorPage<MachineEnrolmentToken> list(ListMachineEnrolmentTokensParams params) {
            return t.get("/v1/machine_enrolment_tokens", params == null ? null : params.toQuery(), new TypeReference<>() {
            });
        }

        /** {@code DELETE /v1/machine_enrolment_tokens/{id}} — Delete a machine enrolment token; it can no longer enrol new devices. */
        public DeletedObject delete(String id) {
            return t.delete("/v1/machine_enrolment_tokens/" + enc(id), new TypeReference<>() {
            });
        }
    }
}
