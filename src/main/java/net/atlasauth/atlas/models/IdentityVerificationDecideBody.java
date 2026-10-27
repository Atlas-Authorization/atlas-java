package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/identity_verifications/{id}/decide}. */
public final class IdentityVerificationDecideBody {
    public String decision;
    public String reason;

    public IdentityVerificationDecideBody() {
    }

    public IdentityVerificationDecideBody decision(String v) { this.decision = v; return this; }
    public IdentityVerificationDecideBody reason(String v) { this.reason = v; return this; }
}
