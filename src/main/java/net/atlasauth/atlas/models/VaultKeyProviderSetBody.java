package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/vault/key_provider}. */
public final class VaultKeyProviderSetBody {
    public String provider;
    public Map<String, Object> config;
    public Map<String, Object> credentials;

    public VaultKeyProviderSetBody() {
    }

    public VaultKeyProviderSetBody provider(String v) { this.provider = v; return this; }
    public VaultKeyProviderSetBody config(Map<String, Object> v) { this.config = v; return this; }
    public VaultKeyProviderSetBody credentials(Map<String, Object> v) { this.credentials = v; return this; }
}
