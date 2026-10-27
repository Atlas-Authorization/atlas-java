package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/iga/requests}. Required: entitlement, userId. */
public final class IgaRequestsCreateBody {
    /** Required. */
    public String entitlement;
    /** Required. */
    public String userId;
    public String requesterUserId;
    public String justification;
    public Long durationSeconds;

    public IgaRequestsCreateBody() {
    }

    public IgaRequestsCreateBody entitlement(String v) { this.entitlement = v; return this; }
    public IgaRequestsCreateBody userId(String v) { this.userId = v; return this; }
    public IgaRequestsCreateBody requesterUserId(String v) { this.requesterUserId = v; return this; }
    public IgaRequestsCreateBody justification(String v) { this.justification = v; return this; }
    public IgaRequestsCreateBody durationSeconds(Long v) { this.durationSeconds = v; return this; }
}
