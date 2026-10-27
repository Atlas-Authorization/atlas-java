package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.VaultKeyProviderGetResponse;
import net.atlasauth.atlas.models.VaultKeyProviderRemoveResponse;
import net.atlasauth.atlas.models.VaultKeyProviderSetBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/vault} resource. */
public final class VaultKeyProvider {

    private final Transport t;

    public VaultKeyProvider(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/vault/key_provider} - The customer-managed key that wraps new vault data keys. 404 when none is configured (the instance key is used). Scope: {@code vault:manage}. */
    public VaultKeyProviderGetResponse get() {
        return t.request("GET", "/v1/vault/key_provider", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/vault/key_provider} - Bring your own key: wrap new vault data keys with your AWS KMS (`config.region`, `config.key_id`; symmetric key), Google Cloud KMS (`config.crypto_key`; symmetric key) or Azure Key Vault (`config.vault_url`, `config.key_name`; RSA key with wrap/unwrap) key. Atlas proves access with a wrap/unwrap round trip before saving (422 or 502 on refusal). Credentials are write-only. Existing versions stay on the key that wrapped them; switching provider type is a 409 while versions remain on the old one. Scope: {@code vault:manage}. */
    public VaultKeyProviderGetResponse set(VaultKeyProviderSetBody body) {
        return t.request("PUT", "/v1/vault/key_provider", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/vault/key_provider} - Stop using your own key for new versions. 409 while any version is still wrapped by it. Scope: {@code vault:manage}. */
    public VaultKeyProviderRemoveResponse remove() {
        return t.request("DELETE", "/v1/vault/key_provider", null, null, null, false, new TypeReference<>() {
        });
    }
}
