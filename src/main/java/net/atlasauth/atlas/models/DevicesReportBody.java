package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/devices}. */
public final class DevicesReportBody {
    public String externalId;
    public String source;
    public String userId;
    public String userEmail;
    public String name;
    public String platform;
    public Boolean managed;
    public Boolean compliant;
    public Map<String, Object> posture;

    public DevicesReportBody() {
    }

    public DevicesReportBody externalId(String v) { this.externalId = v; return this; }
    public DevicesReportBody source(String v) { this.source = v; return this; }
    public DevicesReportBody userId(String v) { this.userId = v; return this; }
    public DevicesReportBody userEmail(String v) { this.userEmail = v; return this; }
    public DevicesReportBody name(String v) { this.name = v; return this; }
    public DevicesReportBody platform(String v) { this.platform = v; return this; }
    public DevicesReportBody managed(Boolean v) { this.managed = v; return this; }
    public DevicesReportBody compliant(Boolean v) { this.compliant = v; return this; }
    public DevicesReportBody posture(Map<String, Object> v) { this.posture = v; return this; }
}
