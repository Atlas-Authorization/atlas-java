package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The org security policy patch (&sect;4.4). Unlike the surrounding routes these
 * fields are camelCase <em>on the wire</em>, so each is pinned with
 * {@code @JsonProperty}. Additional keys can be supplied through {@link #set}.
 */
public final class OrganizationPolicyPatch {
    @JsonProperty("requireMfa")
    public Boolean requireMfa;
    @JsonProperty("ssoRequired")
    public Boolean ssoRequired;
    @JsonProperty("sessionIdleOverrideMs")
    public Long sessionIdleOverrideMs;

    private final Map<String, Object> extra = new LinkedHashMap<>();

    public OrganizationPolicyPatch requireMfa(Boolean v) { this.requireMfa = v; return this; }
    public OrganizationPolicyPatch ssoRequired(Boolean v) { this.ssoRequired = v; return this; }
    public OrganizationPolicyPatch sessionIdleOverrideMs(Long v) { this.sessionIdleOverrideMs = v; return this; }

    /** Set an additional, forward-compatible policy key. */
    public OrganizationPolicyPatch set(String key, Object value) { this.extra.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> extra() { return extra; }
}
