package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/iga/requests/{id}/decide}. Required: decision. */
public final class IgaRequestsDecideBody {
    /** Required. */
    public String decision;
    public String comment;
    public String approverUserId;

    public IgaRequestsDecideBody() {
    }

    public IgaRequestsDecideBody decision(String v) { this.decision = v; return this; }
    public IgaRequestsDecideBody comment(String v) { this.comment = v; return this; }
    public IgaRequestsDecideBody approverUserId(String v) { this.approverUserId = v; return this; }
}
