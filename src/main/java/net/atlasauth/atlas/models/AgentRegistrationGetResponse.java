package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/agent_registration}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AgentRegistrationGetResponse(
        String object,
        Boolean enabled,
        Boolean allowAnonymous,
        Boolean allowServiceAuth,
        Boolean allowRefresh,
        List<String> untrustedScopes,
        List<String> trustedScopes,
        Long claimTtlSeconds,
        List<AgentRegistrationGetResponseAutoApproveRulesItem> autoApproveRules) {
}
