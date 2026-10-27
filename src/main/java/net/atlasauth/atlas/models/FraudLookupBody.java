package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code POST /v1/fraud/lookup}; {@code ip} is required. */
public final class FraudLookupBody {
    public String ip;
    public String userAgent;
    /** The stable id from the JS SDK. */
    public String deviceId;
    public String email;
    /** A user of this instance, for user-based rules. */
    public String userId;
    public String orgId;
    /** Device features reported by the SDK. */
    public Map<String, Object> device;

    public FraudLookupBody() {
    }

    public FraudLookupBody(String ip) {
        this.ip = ip;
    }

    public FraudLookupBody ip(String v) { this.ip = v; return this; }
    public FraudLookupBody userAgent(String v) { this.userAgent = v; return this; }
    public FraudLookupBody deviceId(String v) { this.deviceId = v; return this; }
    public FraudLookupBody email(String v) { this.email = v; return this; }
    public FraudLookupBody userId(String v) { this.userId = v; return this; }
    public FraudLookupBody orgId(String v) { this.orgId = v; return this; }
    public FraudLookupBody device(Map<String, Object> v) { this.device = v; return this; }
}
