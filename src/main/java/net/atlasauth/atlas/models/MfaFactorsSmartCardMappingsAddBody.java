package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/users/{id}/smartcard_mappings}. Required: kind, value. */
public final class MfaFactorsSmartCardMappingsAddBody {
    /** Required. */
    public String kind;
    /** Required. */
    public String value;
    /** Issuer DN. Required for kind "serial". */
    public String issuer;
    public String label;

    public MfaFactorsSmartCardMappingsAddBody() {
    }

    public MfaFactorsSmartCardMappingsAddBody kind(String v) { this.kind = v; return this; }
    public MfaFactorsSmartCardMappingsAddBody value(String v) { this.value = v; return this; }
    public MfaFactorsSmartCardMappingsAddBody issuer(String v) { this.issuer = v; return this; }
    public MfaFactorsSmartCardMappingsAddBody label(String v) { this.label = v; return this; }
}
