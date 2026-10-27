package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/vault/objects/{id}}. */
public final class VaultUpdateBody {
    public String value;
    public Double versionCheck;

    public VaultUpdateBody() {
    }

    public VaultUpdateBody value(String v) { this.value = v; return this; }
    public VaultUpdateBody versionCheck(Double v) { this.versionCheck = v; return this; }
}
