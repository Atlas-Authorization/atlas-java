package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/access_policies/simulate}. */
public final class AccessPoliciesSimulateBody {
    public String policyId;
    public String stage;
    public String ip;
    public String applicationId;
    public String userId;
    public List<String> groups;
    public List<String> orgs;
    public List<String> roles;
    public String country;
    public String riskLevel;
    public String deviceTrust;
    public List<String> authMethods;
    public Boolean mfaVerified;
    public List<String> acr;
    public Long now;

    public AccessPoliciesSimulateBody() {
    }

    public AccessPoliciesSimulateBody policyId(String v) { this.policyId = v; return this; }
    public AccessPoliciesSimulateBody stage(String v) { this.stage = v; return this; }
    public AccessPoliciesSimulateBody ip(String v) { this.ip = v; return this; }
    public AccessPoliciesSimulateBody applicationId(String v) { this.applicationId = v; return this; }
    public AccessPoliciesSimulateBody userId(String v) { this.userId = v; return this; }
    public AccessPoliciesSimulateBody groups(List<String> v) { this.groups = v; return this; }
    public AccessPoliciesSimulateBody orgs(List<String> v) { this.orgs = v; return this; }
    public AccessPoliciesSimulateBody roles(List<String> v) { this.roles = v; return this; }
    public AccessPoliciesSimulateBody country(String v) { this.country = v; return this; }
    public AccessPoliciesSimulateBody riskLevel(String v) { this.riskLevel = v; return this; }
    public AccessPoliciesSimulateBody deviceTrust(String v) { this.deviceTrust = v; return this; }
    public AccessPoliciesSimulateBody authMethods(List<String> v) { this.authMethods = v; return this; }
    public AccessPoliciesSimulateBody mfaVerified(Boolean v) { this.mfaVerified = v; return this; }
    public AccessPoliciesSimulateBody acr(List<String> v) { this.acr = v; return this; }
    public AccessPoliciesSimulateBody now(Long v) { this.now = v; return this; }
}
