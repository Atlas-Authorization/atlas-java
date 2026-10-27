package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/vault/objects}. */
public final class VaultCreateBody {
    public String name;
    public String value;
    public Map<String, Object> keyContext;
    public Map<String, Object> metadata;

    public VaultCreateBody() {
    }

    public VaultCreateBody name(String v) { this.name = v; return this; }
    public VaultCreateBody value(String v) { this.value = v; return this; }
    public VaultCreateBody keyContext(Map<String, Object> v) { this.keyContext = v; return this; }
    public VaultCreateBody metadata(Map<String, Object> v) { this.metadata = v; return this; }
}
