package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/iga/grants}. Required: entitlement, userId. */
public final class IgaGrantsCreateBody {
    /** Required. */
    public String entitlement;
    /** Required. */
    public String userId;
    public Long expiresAt;
    public String overrideReason;

    public IgaGrantsCreateBody() {
    }

    public IgaGrantsCreateBody entitlement(String v) { this.entitlement = v; return this; }
    public IgaGrantsCreateBody userId(String v) { this.userId = v; return this; }
    public IgaGrantsCreateBody expiresAt(Long v) { this.expiresAt = v; return this; }
    public IgaGrantsCreateBody overrideReason(String v) { this.overrideReason = v; return this; }
}
