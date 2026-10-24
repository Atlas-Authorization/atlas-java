package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/secret_keys/{id}/rotate}. */
public final class RotateSecretKeyBody extends ExtensibleBody {
    /** How long the old key keeps working (default 72 hours; 0 = it stops now). */
    public Integer overlapSeconds;
    /** {@code atlas} or {@code legacy}. */
    public String keyFormat;

    public RotateSecretKeyBody() {
    }

    public RotateSecretKeyBody overlapSeconds(Integer v) { this.overlapSeconds = v; return this; }
    public RotateSecretKeyBody keyFormat(String v) { this.keyFormat = v; return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public RotateSecretKeyBody set(String key, Object value) { putField(key, value); return this; }

}
