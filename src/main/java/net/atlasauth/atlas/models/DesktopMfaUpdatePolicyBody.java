package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/desktop_mfa/policy}. */
public final class DesktopMfaUpdatePolicyBody {
    public Boolean enabled;
    public String appliesTo;
    public List<String> userIds;
    public List<String> orgIds;
    public List<String> allowedFactors;
    public Boolean offlineEnabled;
    public Double offlineGraceHours;
    public Boolean offlinePinRequired;

    public DesktopMfaUpdatePolicyBody() {
    }

    public DesktopMfaUpdatePolicyBody enabled(Boolean v) { this.enabled = v; return this; }
    public DesktopMfaUpdatePolicyBody appliesTo(String v) { this.appliesTo = v; return this; }
    public DesktopMfaUpdatePolicyBody userIds(List<String> v) { this.userIds = v; return this; }
    public DesktopMfaUpdatePolicyBody orgIds(List<String> v) { this.orgIds = v; return this; }
    public DesktopMfaUpdatePolicyBody allowedFactors(List<String> v) { this.allowedFactors = v; return this; }
    public DesktopMfaUpdatePolicyBody offlineEnabled(Boolean v) { this.offlineEnabled = v; return this; }
    public DesktopMfaUpdatePolicyBody offlineGraceHours(Double v) { this.offlineGraceHours = v; return this; }
    public DesktopMfaUpdatePolicyBody offlinePinRequired(Boolean v) { this.offlinePinRequired = v; return this; }
}
