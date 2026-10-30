package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateSecretKeyBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.RotateSecretKeyBody;
import net.atlasauth.atlas.models.SecretKey;
import net.atlasauth.atlas.models.UpdateSecretKeyBody;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * {@code /v1/secret_keys} — scoped instance secret keys: mint one per service with only the scopes it needs,
 * an owner, an expiry and an IP allowlist; rotate with an overlap; revoke at once. The secret is in the
 * create / rotate response only. {@code /v1/instance/secret_keys} is an alias of the same routes.
 */
public final class SecretKeys {
    private final Transport t;

    public SecretKeys(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/secret_keys} — Mint a scoped secret key; the secret is in this response only. */
    public SecretKey create(CreateSecretKeyBody body) {
        return create(body, null);
    }

    /** {@link #create(CreateSecretKeyBody)} with an {@code Idempotency-Key}. */
    public SecretKey create(CreateSecretKeyBody body, String idempotencyKey) {
        return t.post("/v1/secret_keys", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/secret_keys} — The instance's secret keys. */
    public ListPage<SecretKey> list() {
        return t.get("/v1/secret_keys", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/secret_keys/{id}} — One secret key. */
    public SecretKey get(String id) {
        return t.get("/v1/secret_keys/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/secret_keys/{id}} — Change a key's name, owner, expiry or IP allowlist. */
    public SecretKey update(String id, UpdateSecretKeyBody body) {
        return t.patch("/v1/secret_keys/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/secret_keys/{id}/rotate} — Mint a new key with the same scopes; the old one keeps working for 72 hours. */
    public SecretKey rotate(String id) {
        return rotate(id, null, null);
    }

    /** {@code POST /v1/secret_keys/{id}/rotate} — Rotate with a chosen overlap ({@code overlapSeconds} 0 = the old key stops now). */
    public SecretKey rotate(String id, RotateSecretKeyBody body) {
        return rotate(id, body, null);
    }

    /** {@link #rotate(String, RotateSecretKeyBody)} with an {@code Idempotency-Key}. */
    public SecretKey rotate(String id, RotateSecretKeyBody body, String idempotencyKey) {
        return t.post("/v1/secret_keys/" + enc(id) + "/rotate", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/secret_keys/{id}/revoke} — Revoke a key at once. */
    public SecretKey revoke(String id) {
        return t.post("/v1/secret_keys/" + enc(id) + "/revoke", null, new TypeReference<>() {
        });
    }
}
