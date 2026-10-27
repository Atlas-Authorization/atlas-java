package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/agent_registration}. */
public final class AgentRegistrationReplaceBody {
    public Boolean enabled;
    public Boolean allowAnonymous;
    public Boolean allowServiceAuth;
    public Boolean allowRefresh;
    public List<String> untrustedScopes;
    public List<String> trustedScopes;
    public Long claimTtlSeconds;
    /** Access requests from a trusted agent matching a rule are approved automatically. */
    public List<Map<String, Object>> autoApproveRules;

    public AgentRegistrationReplaceBody() {
    }

    public AgentRegistrationReplaceBody enabled(Boolean v) { this.enabled = v; return this; }
    public AgentRegistrationReplaceBody allowAnonymous(Boolean v) { this.allowAnonymous = v; return this; }
    public AgentRegistrationReplaceBody allowServiceAuth(Boolean v) { this.allowServiceAuth = v; return this; }
    public AgentRegistrationReplaceBody allowRefresh(Boolean v) { this.allowRefresh = v; return this; }
    public AgentRegistrationReplaceBody untrustedScopes(List<String> v) { this.untrustedScopes = v; return this; }
    public AgentRegistrationReplaceBody trustedScopes(List<String> v) { this.trustedScopes = v; return this; }
    public AgentRegistrationReplaceBody claimTtlSeconds(Long v) { this.claimTtlSeconds = v; return this; }
    public AgentRegistrationReplaceBody autoApproveRules(List<Map<String, Object>> v) { this.autoApproveRules = v; return this; }
}
