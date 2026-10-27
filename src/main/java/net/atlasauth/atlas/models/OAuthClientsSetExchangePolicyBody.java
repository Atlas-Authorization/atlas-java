package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/oauth_clients/{id}/exchange_policy}. */
public final class OAuthClientsSetExchangePolicyBody {
    public List<String> allowedAudiences;
    public List<String> allowedScopes;
    public List<String> idJagAudiences;
    public Boolean allowImpersonation;
    public Boolean requireMayAct;
    public Long maxDelegationDepth;

    public OAuthClientsSetExchangePolicyBody() {
    }

    public OAuthClientsSetExchangePolicyBody allowedAudiences(List<String> v) { this.allowedAudiences = v; return this; }
    public OAuthClientsSetExchangePolicyBody allowedScopes(List<String> v) { this.allowedScopes = v; return this; }
    public OAuthClientsSetExchangePolicyBody idJagAudiences(List<String> v) { this.idJagAudiences = v; return this; }
    public OAuthClientsSetExchangePolicyBody allowImpersonation(Boolean v) { this.allowImpersonation = v; return this; }
    public OAuthClientsSetExchangePolicyBody requireMayAct(Boolean v) { this.requireMayAct = v; return this; }
    public OAuthClientsSetExchangePolicyBody maxDelegationDepth(Long v) { this.maxDelegationDepth = v; return this; }
}
