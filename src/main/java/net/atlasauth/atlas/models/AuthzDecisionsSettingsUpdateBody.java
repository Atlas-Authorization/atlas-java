package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/authz_decision_settings}. */
public final class AuthzDecisionsSettingsUpdateBody {
    public Boolean enabled;
    public Double sampleRateAllow;
    public Double sampleRateDeny;
    public String inputMode;
    public List<String> redact;
    public Boolean shipToEvents;
    public Long retentionDays;

    public AuthzDecisionsSettingsUpdateBody() {
    }

    public AuthzDecisionsSettingsUpdateBody enabled(Boolean v) { this.enabled = v; return this; }
    public AuthzDecisionsSettingsUpdateBody sampleRateAllow(Double v) { this.sampleRateAllow = v; return this; }
    public AuthzDecisionsSettingsUpdateBody sampleRateDeny(Double v) { this.sampleRateDeny = v; return this; }
    public AuthzDecisionsSettingsUpdateBody inputMode(String v) { this.inputMode = v; return this; }
    public AuthzDecisionsSettingsUpdateBody redact(List<String> v) { this.redact = v; return this; }
    public AuthzDecisionsSettingsUpdateBody shipToEvents(Boolean v) { this.shipToEvents = v; return this; }
    public AuthzDecisionsSettingsUpdateBody retentionDays(Long v) { this.retentionDays = v; return this; }
}
