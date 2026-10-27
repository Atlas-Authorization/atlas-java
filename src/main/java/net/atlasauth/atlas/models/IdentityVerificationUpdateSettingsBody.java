package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/identity_verification/settings}. */
public final class IdentityVerificationUpdateSettingsBody {
    public Boolean enabled;
    public String connectorId;
    public Boolean asChallenge;

    public IdentityVerificationUpdateSettingsBody() {
    }

    public IdentityVerificationUpdateSettingsBody enabled(Boolean v) { this.enabled = v; return this; }
    public IdentityVerificationUpdateSettingsBody connectorId(String v) { this.connectorId = v; return this; }
    public IdentityVerificationUpdateSettingsBody asChallenge(Boolean v) { this.asChallenge = v; return this; }
}
