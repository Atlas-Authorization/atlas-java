package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The org security policy patch (&sect;4.4), a partial merge. Unlike the surrounding
 * routes these fields are camelCase <em>on the wire</em>, so each is pinned with
 * {@code @JsonProperty}. Unset fields are left as stored; {@link #clear(String)}
 * sends an explicit {@code null}, which removes that override. Additional keys can
 * be supplied through {@link #set}.
 */
public final class OrganizationPolicyPatch {
    @JsonProperty("requireMfa")
    public Boolean requireMfa;
    @JsonProperty("ssoRequired")
    public Boolean ssoRequired;
    @JsonProperty("sessionIdleOverrideMs")
    public Long sessionIdleOverrideMs;
    /** e.g. {@code "password"}, {@code "passkey"}, {@code "oauth:google"}, {@code "saml"}; an unknown method is a 400. */
    @JsonProperty("allowedSignInMethods")
    public List<String> allowedSignInMethods;
    /** CIDR ranges members may sign in from. */
    @JsonProperty("ipAllowlist")
    public List<String> ipAllowlist;
    @JsonProperty("maxSessionAgeSeconds")
    public Long maxSessionAgeSeconds;
    @JsonProperty("endUserPool")
    public EndUserPoolPolicy endUserPool;

    @JsonIgnore
    private final Map<String, Object> extra = new LinkedHashMap<>();

    public OrganizationPolicyPatch requireMfa(Boolean v) { this.requireMfa = v; return this; }
    public OrganizationPolicyPatch ssoRequired(Boolean v) { this.ssoRequired = v; return this; }
    public OrganizationPolicyPatch sessionIdleOverrideMs(Long v) { this.sessionIdleOverrideMs = v; return this; }
    public OrganizationPolicyPatch allowedSignInMethods(List<String> v) { this.allowedSignInMethods = v; return this; }
    public OrganizationPolicyPatch ipAllowlist(List<String> v) { this.ipAllowlist = v; return this; }
    public OrganizationPolicyPatch maxSessionAgeSeconds(Long v) { this.maxSessionAgeSeconds = v; return this; }
    public OrganizationPolicyPatch endUserPool(EndUserPoolPolicy v) { this.endUserPool = v; return this; }

    /** Set an additional, forward-compatible policy key. */
    public OrganizationPolicyPatch set(String key, Object value) { this.extra.put(key, value); return this; }

    /** Remove a stored override (sends {@code "key": null}), e.g. {@code clear("ipAllowlist")}. */
    public OrganizationPolicyPatch clear(String key) { this.extra.put(key, null); return this; }

    @JsonAnyGetter
    public Map<String, Object> extra() { return extra; }
}
