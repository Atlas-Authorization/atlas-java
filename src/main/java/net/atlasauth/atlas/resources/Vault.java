package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.VaultCreateBody;
import net.atlasauth.atlas.models.VaultCreateResponse;
import net.atlasauth.atlas.models.VaultDeleteResponse;
import net.atlasauth.atlas.models.VaultListResponse;
import net.atlasauth.atlas.models.VaultReadResponse;
import net.atlasauth.atlas.models.VaultUpdateBody;
import net.atlasauth.atlas.models.VaultVersionsResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/vault} resource. */
public final class Vault {

    private final Transport t;

    public Vault(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/vault/objects} - Store a secret. `key_context` (1-10 string labels) is mandatory and is authenticated into the encryption: the value can only be opened with the context it was written under. The value (up to 64 KiB) is sealed under a fresh data key wrapped by the instance key, or by your own cloud KMS key when one is configured. 409 when the name is taken. Scope: {@code vault:write}. */
    public VaultCreateResponse create(VaultCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public VaultCreateResponse create(VaultCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/vault/objects", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/vault/objects} - List vault objects (metadata only, never values), newest first. Filter by `name_prefix` or by `key_context` (a JSON object; objects whose context contains every given label). Scope: {@code vault:list}. */
    public VaultListResponse list() {
        return list(null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public VaultListResponse list(String namePrefix, String keyContext, String startingAfter, Long limit) {
        Map<String, Object> q = Params.of().set("name_prefix", namePrefix).set("key_context", keyContext).set("starting_after", startingAfter).set("limit", limit).map();
        return t.request("GET", "/v1/vault/objects", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/vault/objects/{id}/metadata} - Read a vault object's metadata without its value. Scope: {@code vault:list}. */
    public VaultCreateResponse metadata(String id) {
        return t.request("GET", "/v1/vault/objects/" + enc(id) + "/metadata", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/vault/objects/{id}/versions} - List the versions of a vault object (number, which key protects it, when written) - never values. Scope: {@code vault:list}. */
    public VaultVersionsResponse versions(String id) {
        return versions(id, null, null);
    }

    /** As above, with every optional parameter. */
    public VaultVersionsResponse versions(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/vault/objects/" + enc(id) + "/versions", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/vault/objects/{id}} - Read a vault object with its decrypted value (the current version, or `version`). Every read is audited. 409 if the version's wrapping key provider is no longer configured; 502 VAULT_KMS_ERROR when your KMS refuses. Scope: {@code vault:read}. */
    public VaultReadResponse read(String id) {
        return read(id, null);
    }

    /** As above, with every optional parameter. */
    public VaultReadResponse read(String id, Long version) {
        Map<String, Object> q = Params.of().set("version", version).map();
        return t.request("GET", "/v1/vault/objects/" + enc(id), q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/vault/objects/{id}} - Append a new version with a new value. Earlier versions stay readable. Pass `version_check` (the version you last read) for optimistic concurrency: 409 if another update landed since. Scope: {@code vault:write}. */
    public VaultCreateResponse update(String id, VaultUpdateBody body) {
        return t.request("PUT", "/v1/vault/objects/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/vault/objects/{id}} - Delete a vault object and every version of it. Irreversible. Scope: {@code vault:delete}. */
    public VaultDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/vault/objects/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** The {@code keyProvider} sub-resource. */
    public VaultKeyProvider keyProvider() {
        return new VaultKeyProvider(t);
    }
}
