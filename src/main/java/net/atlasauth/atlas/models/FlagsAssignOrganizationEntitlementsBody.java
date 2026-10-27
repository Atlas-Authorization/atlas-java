package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/organizations/{id}/entitlements}. */
public final class FlagsAssignOrganizationEntitlementsBody {
    public String plan;
    public List<String> features;
    public Map<String, Object> limits;
    /** Unix ms or ISO 8601; null = no expiry. */
    public Object expiresAt;

    public FlagsAssignOrganizationEntitlementsBody() {
    }

    public FlagsAssignOrganizationEntitlementsBody plan(String v) { this.plan = v; return this; }
    public FlagsAssignOrganizationEntitlementsBody features(List<String> v) { this.features = v; return this; }
    public FlagsAssignOrganizationEntitlementsBody limits(Map<String, Object> v) { this.limits = v; return this; }
    public FlagsAssignOrganizationEntitlementsBody expiresAt(Object v) { this.expiresAt = v; return this; }
}
